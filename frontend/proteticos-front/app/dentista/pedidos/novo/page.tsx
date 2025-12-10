// app/dentista/pedidos/novo/page.tsx
"use client"

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'

interface Protetico {
  id: number
  nome: string
  especializacao: string
}

export default function NovoPedido() {
  const router = useRouter()
  const [proteticos, setProteticos] = useState<Protetico[]>([])
  const [carregando, setCarregando] = useState(false)
  const [dados, setDados] = useState({
    proteticoId: '',
    tipoServico: '',
    informacoesDetalhadas: '',
    dataPrevistaEntrega: '',
    valorCobrado: ''
  })

  useEffect(() => {
    carregarProteticos()
  }, [])

  const carregarProteticos = async () => {
    try {
      // TODO: Implementar API para buscar protéticos
      const response = await fetch('http://localhost:8080/api/proteticos')
      if (response.ok) {
        const data = await response.json()
        setProteticos(data)
      }
    } catch (error) {
      console.error('Erro ao carregar protéticos:', error)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setCarregando(true)

    try {
      const usuario = JSON.parse(localStorage.getItem('usuario') || '{}')
      
      const response = await fetch('http://localhost:8080/api/pedidos/novo', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          proteticoId: parseInt(dados.proteticoId),
          tipoServico: dados.tipoServico,
          informacoesDetalhadas: dados.informacoesDetalhadas,
          dataPrevistaEntrega: dados.dataPrevistaEntrega,
          valorCobrado: dados.valorCobrado ? parseFloat(dados.valorCobrado) : null,
          dentistaId: usuario.id
        })
      })

      if (response.ok) {
        alert('Pedido criado com sucesso!')
        router.push('/dentista/pedidos')
      } else {
        const erro = await response.text()
        alert(`Erro: ${erro}`)
      }
    } catch (error) {
      console.error('Erro ao criar pedido:', error)
      alert('Erro ao conectar com o servidor')
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <button 
            onClick={() => router.push('/dentista/pedidos')}
            className="flex items-center text-blue-600 hover:text-blue-800 mb-4"
          >
            ← Voltar para Pedidos
          </button>
          <h1 className="text-2xl font-bold text-gray-900">Novo Pedido de Prótese</h1>
          <p className="text-gray-600">Preencha os dados do novo pedido</p>
        </div>

        {/* Formulário */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Protético */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Protético Responsável *
              </label>
              <select
                value={dados.proteticoId}
                onChange={(e) => setDados({...dados, proteticoId: e.target.value})}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required
              >
                <option value="">Selecione um protético</option>
                {proteticos.map((protetico) => (
                  <option key={protetico.id} value={protetico.id}>
                    {protetico.nome} - {protetico.especializacao}
                  </option>
                ))}
              </select>
            </div>

            {/* Tipo de Serviço */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Tipo de Prótese *
              </label>
              <select
                value={dados.tipoServico}
                onChange={(e) => setDados({...dados, tipoServico: e.target.value})}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required
              >
                <option value="">Selecione o tipo</option>
                <option value="COROA">Coroa</option>
                <option value="PONTE">Ponte</option>
                <option value="PROTESE_TOTAL">Prótese Total</option>
                <option value="PROTESE_PARCIAL">Prótese Parcial</option>
                <option value="FACETA">Faceta</option>
                <option value="ONLAY">Onlay/Inlay</option>
                <option value="IMPLANTE">Prótese sobre Implante</option>
              </select>
            </div>

            {/* Data Prevista */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Data Prevista para Entrega
              </label>
              <input
                type="date"
                value={dados.dataPrevistaEntrega}
                onChange={(e) => setDados({...dados, dataPrevistaEntrega: e.target.value})}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                min={new Date().toISOString().split('T')[0]}
              />
            </div>

            {/* Valor */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Valor Cobrado (R$)
              </label>
              <input
                type="number"
                step="0.01"
                value={dados.valorCobrado}
                onChange={(e) => setDados({...dados, valorCobrado: e.target.value})}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="0,00"
              />
            </div>

            {/* Informações Detalhadas */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Informações Detalhadas *
              </label>
              <textarea
                value={dados.informacoesDetalhadas}
                onChange={(e) => setDados({...dados, informacoesDetalhadas: e.target.value})}
                rows={6}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Descreva detalhes do caso, cor desejada, material, dentes envolvidos, observações importantes..."
                required
              />
            </div>

            {/* Botões */}
            <div className="flex justify-end space-x-4 pt-6 border-t border-gray-200">
              <button
                type="button"
                onClick={() => router.push('/dentista/pedidos')}
                className="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={carregando}
                className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-2 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 shadow-md disabled:opacity-50"
              >
                {carregando ? 'Criando...' : 'Criar Pedido'}
              </button>
            </div>
          </form>
        </div>

        {/* Informações de Ajuda */}
        <div className="mt-6 bg-blue-50 rounded-lg p-4">
          <h3 className="font-semibold text-blue-900 mb-2">💡 Dicas para um bom pedido:</h3>
          <ul className="text-sm text-blue-800 space-y-1">
            <li>• Seja específico sobre cores e materiais desejados</li>
            <li>• Inclua informações sobre o paciente quando relevante</li>
            <li>• Especifique prazos importantes</li>
            <li>• Adicione observações sobre ajustes ou particularidades</li>
          </ul>
        </div>
      </div>
    </div>
  )
}