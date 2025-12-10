// app/dentista/pedidos/[id]/page.tsx
"use client"

import { useEffect, useState } from 'react'
import { useParams, useRouter } from 'next/navigation'

interface Pedido {
  id: number
  codigo: string
  tipoServico: string
  status: string
  informacoesDetalhadas: string
  valorCobrado: number
  dataEntrada: string
  dataPrevistaEntrega: string
  dataEntrega: string
  protetico: {
    nome: string
    email: string
    telefone: string
  }
  etapas: Array<{
    id: number
    nome: string
    status: string
    dataConclusao: string
    observacoes: string
  }>
}

export default function DetalhesPedido() {
  const params = useParams()
  const router = useRouter()
  const id = params.id as string
  const [pedido, setPedido] = useState<Pedido | null>(null)
  const [carregando, setCarregando] = useState(true)

  useEffect(() => {
    carregarPedido()
  }, [id])

  const carregarPedido = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/pedidos/${id}`)
      if (response.ok) {
        const data = await response.json()
        setPedido(data)
      } else {
        alert('Pedido não encontrado')
        router.push('/dentista/pedidos')
      }
    } catch (error) {
      console.error('Erro ao carregar pedido:', error)
      alert('Erro ao carregar pedido')
    } finally {
      setCarregando(false)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'FINALIZADO': return 'bg-green-100 text-green-800'
      case 'EM_PRODUCAO': return 'bg-blue-100 text-blue-800'
      case 'AGUARDANDO_APROVACAO': return 'bg-yellow-100 text-yellow-800'
      case 'RASCUNHO': return 'bg-gray-100 text-gray-800'
      case 'CANCELADO': return 'bg-red-100 text-red-800'
      default: return 'bg-purple-100 text-purple-800'
    }
  }

  const getStatusEtapaColor = (status: string) => {
    switch (status) {
      case 'CONCLUIDA': return 'bg-green-500'
      case 'EM_ANDAMENTO': return 'bg-blue-500'
      case 'PENDENTE': return 'bg-gray-300'
      default: return 'bg-yellow-500'
    }
  }

  const formatarValor = (valor: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor)
  }

  if (carregando) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Carregando pedido...</p>
        </div>
      </div>
    )
  }

  if (!pedido) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-red-600">Pedido não encontrado</h2>
          <button 
            onClick={() => router.push('/dentista/pedidos')}
            className="mt-4 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
          >
            Voltar para Pedidos
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="flex justify-between items-start mb-8">
          <div>
            <button 
              onClick={() => router.push('/dentista/pedidos')}
              className="flex items-center text-blue-600 hover:text-blue-800 mb-4"
            >
              ← Voltar para Pedidos
            </button>
            <div className="flex items-center space-x-4">
              <h1 className="text-2xl font-bold text-gray-900">Pedido {pedido.codigo}</h1>
              <span className={`inline-flex px-3 py-1 text-sm font-semibold rounded-full ${getStatusColor(pedido.status)}`}>
                {pedido.status.replace('_', ' ')}
              </span>
            </div>
            <p className="text-gray-600 mt-1">Detalhes completos do pedido</p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Coluna 1: Informações Principais */}
          <div className="lg:col-span-2 space-y-6">
            {/* Card Informações do Pedido */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">📋 Informações do Pedido</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium text-gray-500">Tipo de Prótese</label>
                  <p className="text-gray-900">{pedido.tipoServico}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-gray-500">Valor</label>
                  <p className="text-gray-900 font-semibold">
                    {pedido.valorCobrado ? formatarValor(pedido.valorCobrado) : 'Não definido'}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium text-gray-500">Data de Entrada</label>
                  <p className="text-gray-900">{new Date(pedido.dataEntrada).toLocaleDateString('pt-BR')}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-gray-500">Previsão de Entrega</label>
                  <p className="text-gray-900">
                    {pedido.dataPrevistaEntrega 
                      ? new Date(pedido.dataPrevistaEntrega).toLocaleDateString('pt-BR')
                      : 'Não definida'
                    }
                  </p>
                </div>
                {pedido.dataEntrega && (
                  <div>
                    <label className="text-sm font-medium text-gray-500">Data de Entrega</label>
                    <p className="text-gray-900">{new Date(pedido.dataEntrega).toLocaleDateString('pt-BR')}</p>
                  </div>
                )}
              </div>
            </div>

            {/* Card Detalhes e Observações */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">📝 Detalhes e Observações</h3>
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="text-gray-800 whitespace-pre-wrap">
                  {pedido.informacoesDetalhadas || 'Nenhuma observação adicional.'}
                </p>
              </div>
            </div>
          </div>

          {/* Coluna 2: Informações do Protético e Etapas */}
          <div className="space-y-6">
            {/* Card Protético Responsável */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">👨‍🔬 Protético Responsável</h3>
              <div className="space-y-3">
                <div>
                  <label className="text-sm font-medium text-gray-500">Nome</label>
                  <p className="text-gray-900">{pedido.protetico.nome}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-gray-500">Email</label>
                  <p className="text-gray-900">{pedido.protetico.email}</p>
                </div>
                {pedido.protetico.telefone && (
                  <div>
                    <label className="text-sm font-medium text-gray-500">Telefone</label>
                    <p className="text-gray-900">{pedido.protetico.telefone}</p>
                  </div>
                )}
              </div>
            </div>

            {/* Card Andamento do Pedido */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">📊 Andamento do Pedido</h3>
              <div className="space-y-4">
                {pedido.etapas && pedido.etapas.length > 0 ? (
                  pedido.etapas.map((etapa, index) => (
                    <div key={etapa.id} className="flex items-start space-x-3">
                      <div className={`w-3 h-3 rounded-full mt-1.5 ${getStatusEtapaColor(etapa.status)}`}></div>
                      <div className="flex-1">
                        <div className="flex justify-between items-start">
                          <span className="text-sm font-medium text-gray-900">{etapa.nome}</span>
                          {etapa.dataConclusao && (
                            <span className="text-xs text-gray-500">
                              {new Date(etapa.dataConclusao).toLocaleDateString('pt-BR')}
                            </span>
                          )}
                        </div>
                        {etapa.observacoes && (
                          <p className="text-xs text-gray-600 mt-1">{etapa.observacoes}</p>
                        )}
                      </div>
                    </div>
                  ))
                ) : (
                  <p className="text-gray-500 text-sm">Nenhuma etapa registrada ainda.</p>
                )}
              </div>
            </div>

            {/* Card Ações */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">⚡ Ações</h3>
              <div className="space-y-3">
                <button className="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors">
                  📞 Entrar em Contato
                </button>
                <button className="w-full border border-gray-300 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-50 transition-colors">
                  ✏️ Solicitar Alteração
                </button>
                {pedido.status !== 'CANCELADO' && (
                  <button className="w-full border border-red-300 text-red-700 py-2 px-4 rounded-lg hover:bg-red-50 transition-colors">
                    🗑️ Cancelar Pedido
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}