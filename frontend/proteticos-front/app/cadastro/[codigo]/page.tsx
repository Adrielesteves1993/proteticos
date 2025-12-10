// app/cadastro/[codigo]/page.tsx
'use client'

import { useState, useEffect } from 'react'
import { useParams, useRouter } from 'next/navigation'

interface Convite {
  id: number
  codigo: string
  tipo: 'DENTISTA' | 'PROTETICO'
  emailConvidado: string | null
  criadoEm: string
  expiraEm: string
  utilizado: boolean
}

export default function CadastroPorConvite() {
  const params = useParams()
  const router = useRouter()
  const codigo = params.codigo as string
  
  const [convite, setConvite] = useState<Convite | null>(null)
  const [carregando, setCarregando] = useState(true)
  const [enviando, setEnviando] = useState(false)
  const [dados, setDados] = useState({
    nome: '',
    email: '',
    senha: '',
    confirmarSenha: ''
  })

  useEffect(() => {
    validarConvite()
  }, [codigo])

  const validarConvite = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/convites/${codigo}/validar`)
      if (response.ok) {
        const conviteData = await response.json()
        setConvite(conviteData)
        if (conviteData.emailConvidado) {
          setDados(prev => ({ ...prev, email: conviteData.emailConvidado }))
        }
      } else {
        alert('Convite inválido, expirado ou já utilizado')
        router.push('/')
      }
    } catch (error) {
      alert('Erro ao validar convite')
      console.error(error)
    } finally {
      setCarregando(false)
    }
  }

  const handleCadastro = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (dados.senha !== dados.confirmarSenha) {
      alert('As senhas não coincidem')
      return
    }

    if (dados.senha.length < 6) {
      alert('A senha deve ter pelo menos 6 caracteres')
      return
    }

    setEnviando(true)
    try {
      // ✅ CHAMADA CORRIGIDA PARA A API
      const response = await fetch('http://localhost:8080/api/cadastro', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nome: dados.nome,
          email: dados.email,
          senha: dados.senha,
          codigoConvite: codigo,
          tipoUsuario: convite?.tipo
        })
      })

      const result = await response.json()

      if (response.ok) {
        alert('Cadastro realizado com sucesso!')
        router.push('/login')
      } else {
        alert(`Erro: ${result.error}`)
      }
    } catch (error) {
      alert('Erro ao conectar com o servidor')
      console.error(error)
    } finally {
      setEnviando(false)
    }
  }

  if (carregando) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Validando convite...</p>
        </div>
      </div>
    )
  }

  if (!convite) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-red-600">Convite Inválido</h2>
          <p className="mt-2 text-gray-600">Este convite não é válido ou já expirou.</p>
          <button 
            onClick={() => router.push('/')}
            className="mt-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            Voltar para Home
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Cadastro - {convite.tipo === 'DENTISTA' ? '👨‍⚕️ Dentista' : '🦷 Protético'}
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Convite: <span className="font-mono bg-gray-100 px-2 py-1 rounded">{codigo}</span>
          </p>
          {convite.emailConvidado && (
            <p className="mt-1 text-center text-sm text-green-600">
              Email convidado: {convite.emailConvidado}
            </p>
          )}
        </div>
        
        <form className="mt-8 space-y-6" onSubmit={handleCadastro}>
          <div className="space-y-4">
            <div>
              <label htmlFor="nome" className="block text-sm font-medium text-gray-700">
                Nome Completo
              </label>
              <input
                id="nome"
                type="text"
                value={dados.nome}
                onChange={(e) => setDados({...dados, nome: e.target.value})}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>
            
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                Email
              </label>
              <input
                id="email"
                type="email"
                value={dados.email}
                onChange={(e) => setDados({...dados, email: e.target.value})}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                required
                disabled={!!convite.emailConvidado}
              />
              {convite.emailConvidado && (
                <p className="mt-1 text-xs text-gray-500">
                  Email definido pelo convite
                </p>
              )}
            </div>
            
            <div>
              <label htmlFor="senha" className="block text-sm font-medium text-gray-700">
                Senha
              </label>
              <input
                id="senha"
                type="password"
                value={dados.senha}
                onChange={(e) => setDados({...dados, senha: e.target.value})}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                required
                minLength={6}
              />
            </div>

            <div>
              <label htmlFor="confirmarSenha" className="block text-sm font-medium text-gray-700">
                Confirmar Senha
              </label>
              <input
                id="confirmarSenha"
                type="password"
                value={dados.confirmarSenha}
                onChange={(e) => setDados({...dados, confirmarSenha: e.target.value})}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={enviando}
            className="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
          >
            {enviando ? 'Criando conta...' : 'Criar Conta'}
          </button>
        </form>
      </div>
    </div>
  )
}