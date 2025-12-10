// app/convites/gerar/page.tsx
"use client"

import { useState } from 'react'
import { useRouter } from 'next/navigation'

type UserTipo = 'DENTISTA' | 'PROTETICO'

interface ConviteResponse {
  message: string
  codigo: string
  tipo: UserTipo
  expiraEm: string
  link: string
}

export default function GerarConvitePage() {
  const router = useRouter()
  const [carregando, setCarregando] = useState(false)
  const [conviteGerado, setConviteGerado] = useState<ConviteResponse | null>(null)
  
  const [formData, setFormData] = useState({
    tipo: 'DENTISTA' as UserTipo,
    emailConvidado: '',
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setCarregando(true)

    try {
      const response = await fetch('http://localhost:8080/api/convites/gerar-teste', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          tipo: formData.tipo,
          emailConvidado: formData.emailConvidado || null,
        })
      })

      if (response.ok) {
        const data = await response.json()
        setConviteGerado(data)
      } else {
        const erro = await response.json()
        alert(`Erro: ${erro.error || 'Erro ao gerar convite'}`)
      }
    } catch (error) {
      console.error('Erro ao gerar convite:', error)
      alert('Erro ao conectar com o servidor')
    } finally {
      setCarregando(false)
    }
  }

  const copiarCodigo = () => {
    if (conviteGerado?.codigo) {
      navigator.clipboard.writeText(conviteGerado.codigo)
      alert('Código copiado para a área de transferência!')
    }
  }

  const copiarLink = () => {
    if (conviteGerado?.link) {
      navigator.clipboard.writeText(conviteGerado.link)
      alert('Link copiado para a área de transferência!')
    }
  }

  const formatarData = (dataString: string) => {
    const data = new Date(dataString)
    return data.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    })
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-blue-50 p-4 md:p-8">
      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <button 
            onClick={() => router.back()}
            className="flex items-center text-blue-600 hover:text-blue-800 mb-4"
          >
            ← Voltar
          </button>
          <h1 className="text-3xl font-bold text-gray-900">Gerar Novo Convite</h1>
          <p className="text-gray-600 mt-2">
            Gere códigos de convite para novos dentistas ou protéticos
          </p>
        </div>

        {/* Formulário */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-200 p-6 mb-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Tipo de Usuário */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Tipo de Usuário *
              </label>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <button
                  type="button"
                  onClick={() => setFormData({...formData, tipo: 'DENTISTA'})}
                  className={`p-4 rounded-lg border-2 transition-all ${formData.tipo === 'DENTISTA' 
                    ? 'border-blue-500 bg-blue-50' 
                    : 'border-gray-200 hover:border-gray-300'}`}
                >
                  <div className="flex items-center">
                    <div className={`w-10 h-10 rounded-lg flex items-center justify-center mr-3 ${formData.tipo === 'DENTISTA' ? 'bg-blue-100' : 'bg-gray-100'}`}>
                      <span className="text-xl">🦷</span>
                    </div>
                    <div className="text-left">
                      <h3 className="font-semibold text-gray-900">Dentista</h3>
                      <p className="text-sm text-gray-600">Clínico odontológico</p>
                    </div>
                  </div>
                </button>
                
                <button
                  type="button"
                  onClick={() => setFormData({...formData, tipo: 'PROTETICO'})}
                  className={`p-4 rounded-lg border-2 transition-all ${formData.tipo === 'PROTETICO' 
                    ? 'border-green-500 bg-green-50' 
                    : 'border-gray-200 hover:border-gray-300'}`}
                >
                  <div className="flex items-center">
                    <div className={`w-10 h-10 rounded-lg flex items-center justify-center mr-3 ${formData.tipo === 'PROTETICO' ? 'bg-green-100' : 'bg-gray-100'}`}>
                      <span className="text-xl">🔧</span>
                    </div>
                    <div className="text-left">
                      <h3 className="font-semibold text-gray-900">Protético</h3>
                      <p className="text-sm text-gray-600">Laboratório dental</p>
                    </div>
                  </div>
                </button>
              </div>
            </div>

            {/* Email (opcional) */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Email do Convidado (opcional)
              </label>
              <input
                type="email"
                value={formData.emailConvidado}
                onChange={(e) => setFormData({...formData, emailConvidado: e.target.value})}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="convidado@email.com"
              />
              <p className="text-sm text-gray-500 mt-1">
                Se informado, apenas este email poderá usar o convite
              </p>
            </div>

            {/* Botões */}
            <div className="flex flex-col md:flex-row gap-4 pt-6">
              <button
                type="button"
                onClick={() => router.back()}
                className="flex-1 px-6 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
              >
                Cancelar
              </button>
              
              <button
                type="submit"
                disabled={carregando}
                className="flex-1 bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-3 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {carregando ? (
                  <span className="flex items-center justify-center">
                    <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Gerando...
                  </span>
                ) : 'Gerar Convite'}
              </button>
            </div>
          </form>
        </div>

        {/* Convite Gerado */}
        {conviteGerado && (
          <div className="bg-gradient-to-r from-green-50 to-emerald-50 rounded-2xl border-2 border-green-200 p-6 animate-fadeIn">
            <div className="flex items-center mb-4">
              <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center mr-3">
                <span className="text-xl text-green-600">🎉</span>
              </div>
              <div>
                <h3 className="text-xl font-bold text-gray-900">Convite Gerado!</h3>
                <p className="text-sm text-green-600">Compartilhe com a pessoa convidada</p>
              </div>
            </div>
            
            <div className="space-y-4">
              {/* Código do Convite */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Código do Convite
                </label>
                <div className="flex">
                  <div className="flex-1 bg-white border border-gray-300 rounded-l-lg px-4 py-3 font-mono text-lg font-bold">
                    {conviteGerado.codigo}
                  </div>
                  <button
                    onClick={copiarCodigo}
                    className="bg-blue-500 text-white px-6 rounded-r-lg hover:bg-blue-600 transition-colors flex items-center"
                  >
                    📋 Copiar
                  </button>
                </div>
              </div>

              {/* Detalhes */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-white rounded-lg p-4 border border-gray-200">
                  <p className="text-sm text-gray-600">Tipo</p>
                  <p className="font-semibold text-gray-900 mt-1">
                    {conviteGerado.tipo === 'DENTISTA' ? '🦷 Dentista' : '🔧 Protético'}
                  </p>
                </div>
                
                <div className="bg-white rounded-lg p-4 border border-gray-200">
                  <p className="text-sm text-gray-600">Expira em</p>
                  <p className="font-semibold text-gray-900 mt-1">
                    {formatarData(conviteGerado.expiraEm)}
                  </p>
                </div>
                
                <div className="bg-white rounded-lg p-4 border border-gray-200">
                  <p className="text-sm text-gray-600">Status</p>
                  <p className="font-semibold text-green-600 mt-1">✅ Disponível</p>
                </div>
              </div>

              {/* Link de Cadastro */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Link de Cadastro
                </label>
                <div className="flex">
                  <div className="flex-1 bg-white border border-gray-300 rounded-l-lg px-4 py-3 text-sm text-gray-600 truncate">
                    {conviteGerado.link}
                  </div>
                  <button
                    onClick={copiarLink}
                    className="bg-green-500 text-white px-6 rounded-r-lg hover:bg-green-600 transition-colors flex items-center"
                  >
                    🔗 Copiar Link
                  </button>
                </div>
              </div>

              {/* Instruções */}
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mt-4">
                <h4 className="font-semibold text-blue-900 mb-2 flex items-center">
                  <span className="mr-2">📋</span> Como usar:
                </h4>
                <ol className="text-sm text-blue-800 space-y-2">
                  <li className="flex items-start">
                    <span className="font-bold mr-2">1.</span>
                    <span>Compartilhe o código ou link acima com a pessoa convidada</span>
                  </li>
                  <li className="flex items-start">
                    <span className="font-bold mr-2">2.</span>
                    <span>A pessoa acessa o link e preenche o cadastro</span>
                  </li>
                  <li className="flex items-start">
                    <span className="font-bold mr-2">3.</span>
                    <span>Ela informa o código no campo "Código de Convite"</span>
                  </li>
                  <li className="flex items-start">
                    <span className="font-bold mr-2">4.</span>
                    <span>Após o cadastro, ela já terá acesso ao sistema</span>
                  </li>
                </ol>
              </div>
            </div>
          </div>
        )}

        {/* Informações */}
        <div className="mt-8 bg-gray-50 rounded-lg p-6 border border-gray-200">
          <h3 className="font-semibold text-gray-900 mb-3 flex items-center">
            <span className="mr-2">💡</span> Informações importantes:
          </h3>
          <ul className="text-sm text-gray-700 space-y-2">
            <li className="flex items-start">
              <span className="text-blue-500 mr-2">•</span>
              <span>Cada convite pode ser usado apenas <strong>uma vez</strong></span>
            </li>
            <li className="flex items-start">
              <span className="text-blue-500 mr-2">•</span>
              <span>Convites expiram automaticamente após <strong>30 dias</strong></span>
            </li>
            <li className="flex items-start">
              <span className="text-blue-500 mr-2">•</span>
              <span>Se o email for especificado, apenas esse email poderá usar o convite</span>
            </li>
            <li className="flex items-start">
              <span className="text-blue-500 mr-2">•</span>
              <span>Você pode gerar até <strong>10 convites ativos</strong> por vez</span>
            </li>
          </ul>
        </div>
      </div>

      <style jsx>{`
        @keyframes fadeIn {
          from {
            opacity: 0;
            transform: translateY(10px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
        .animate-fadeIn {
          animation: fadeIn 0.5s ease-out;
        }
      `}</style>
    </div>
  )
}