// app/dentista/pedidos/page.tsx - VERSÃO CORRIGIDA
"use client"

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'

interface Pedido {
  id: number
  codigo: string
  tipoServico: string
  status: string
  dataEntrada: string
  dataPrevistaEntrega: string
  protetico: {
    nome: string
  }
}

export default function PedidosDentista() {
  const router = useRouter()
  const [pedidos, setPedidos] = useState<Pedido[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState<string | null>(null)

  useEffect(() => {
    carregarPedidos()
  }, [])

  const carregarPedidos = async () => {
    try {
      setCarregando(true)
      setErro(null)
      
      // 1. Pega o dentista logado
      const usuarioJSON = localStorage.getItem('usuario')
      if (!usuarioJSON) {
        throw new Error('Nenhum usuário logado. Faça login novamente.')
      }
      
      const usuario = JSON.parse(usuarioJSON)
      console.log('👤 Usuário logado - ID:', usuario.id, 'Nome:', usuario.nome)
      console.log('🎯 Tipo do usuário (original):', usuario.tipo)
      
      // 2. VERIFICAÇÃO CORRIGIDA: Normaliza para maiúsculas antes de verificar
      const tipoUsuario = String(usuario.tipo || '').toUpperCase()
      console.log('🎯 Tipo normalizado:', tipoUsuario)
      
      // Verifica se é dentista
      if (!tipoUsuario.includes('DENTISTA')) {
        setErro('Apenas dentistas podem acessar esta página.')
        console.warn('⚠️ Tipo de usuário não autorizado:', usuario.tipo)
        setCarregando(false)
        return
      }
      
      // 3. Usa o NOVO endpoint otimizado
      console.log(`🔄 Chamando: http://localhost:8080/api/pedidos/dentista/${usuario.id}`)
      
      const response = await fetch(`http://localhost:8080/api/pedidos/dentista/${usuario.id}`)
      
      if (!response.ok) {
        const errorText = await response.text()
        console.error('❌ Erro na API:', response.status, errorText)
        throw new Error(`Erro ${response.status}: Não foi possível carregar os pedidos`)
      }
      
      const pedidosData = await response.json()
      console.log('✅ Dados recebidos:', pedidosData)
      
      // 4. Verifica se é array
      if (!Array.isArray(pedidosData)) {
        throw new Error('Formato de dados inválido da API')
      }
      
      // 5. Mapeia para o formato da interface (MAIS SIMPLES agora)
      const pedidosFormatados = pedidosData.map((item: any): Pedido => {
        return {
          id: item.id || 0,
          codigo: item.codigo || `P${item.id}`,
          tipoServico: item.tipoServico || 'NÃO_ESPECIFICADO',
          status: item.status || 'RASCUNHO',
          dataEntrada: item.dataEntrada || new Date().toISOString().split('T')[0],
          dataPrevistaEntrega: item.dataPrevistaEntrega,
          protetico: {
            nome: item.protetico?.nome || 'Protético não definido'
          }
        }
      })
      
      // 6. Ordena por ID (mais recente primeiro)
      pedidosFormatados.sort((a, b) => b.id - a.id)
      
      console.log(`📊 ${pedidosFormatados.length} pedidos carregados com sucesso`)
      setPedidos(pedidosFormatados)
      
    } catch (error: any) {
      console.error('💥 Erro ao carregar pedidos:', error)
      setErro(error.message || 'Erro ao carregar pedidos')
      setPedidos([])
    } finally {
      setCarregando(false)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'FINALIZADO': return 'bg-green-100 text-green-800'
      case 'EM_ANDAMENTO': return 'bg-blue-100 text-blue-800'
      case 'RASCUNHO': return 'bg-gray-100 text-gray-800'
      case 'CANCELADO': return 'bg-red-100 text-red-800'
      case 'CONCLUIDO': return 'bg-green-100 text-green-800'
      case 'ENTREGUE': return 'bg-purple-100 text-purple-800'
      case 'EM_PRODUCAO': return 'bg-yellow-100 text-yellow-800'
      default: return 'bg-yellow-100 text-yellow-800'
    }
  }

  const formatarTipoServico = (tipo: string) => {
    return tipo
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, l => l.toUpperCase())
  }

  // Função para recarregar
  const recarregar = () => {
    carregarPedidos()
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Meus Pedidos</h1>
            <p className="text-gray-600">Acompanhe todos os seus pedidos de prótese</p>
          </div>
          <div className="flex gap-2">
            <button 
              onClick={recarregar}
              className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 flex items-center gap-2"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              Recarregar
            </button>
            <button 
              onClick={() => router.push('/dentista/pedidos/novo')}
              className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-3 rounded-lg hover:from-blue-600 hover:to-blue-700 flex items-center gap-2"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              Novo Pedido
            </button>
          </div>
        </div>

        {/* Mensagem de erro */}
        {erro && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
            <div className="flex">
              <div className="flex-shrink-0">
                <svg className="h-5 w-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                </svg>
              </div>
              <div className="ml-3">
                <h3 className="text-sm font-medium text-red-800">{erro}</h3>
                <button 
                  onClick={recarregar}
                  className="mt-2 text-sm text-red-600 hover:text-red-800"
                >
                  Tentar novamente
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Lista de Pedidos */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100">
          {carregando ? (
            <div className="p-12 text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
              <p className="text-gray-600 font-medium">Carregando pedidos da API...</p>
              <p className="text-sm text-gray-500 mt-1">Buscando seus pedidos no sistema</p>
            </div>
          ) : pedidos.length === 0 ? (
            <div className="p-12 text-center">
              <div className="text-6xl mb-4">📋</div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Nenhum pedido encontrado</h3>
              <p className="text-gray-600 mb-6">Você ainda não criou nenhum pedido</p>
              <button 
                onClick={() => router.push('/dentista/pedidos/novo')}
                className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 flex items-center gap-2 mx-auto"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
                Criar Primeiro Pedido
              </button>
            </div>
          ) : (
            <div className="overflow-hidden">
              <div className="p-4 border-b bg-gray-50">
                <p className="text-sm text-gray-600">
                  Mostrando <span className="font-bold">{pedidos.length}</span> pedidos
                </p>
              </div>
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Código
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Tipo
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Protético
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Previsão
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Ações
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {pedidos.map((pedido) => (
                    <tr 
                      key={pedido.id} 
                      className="hover:bg-gray-50 transition-colors cursor-pointer"
                      onClick={() => router.push(`/dentista/pedidos/${pedido.id}`)}
                    >
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-semibold text-gray-900">{pedido.codigo}</div>
                        <div className="text-xs text-gray-500">
                          {new Date(pedido.dataEntrada).toLocaleDateString('pt-BR')}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          {formatarTipoServico(pedido.tipoServico)}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900 font-medium">{pedido.protetico.nome}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex px-3 py-1 text-xs font-semibold rounded-full ${getStatusColor(pedido.status)}`}>
                          {pedido.status.replace(/_/g, ' ')}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          {pedido.dataPrevistaEntrega 
                            ? new Date(pedido.dataPrevistaEntrega).toLocaleDateString('pt-BR')
                            : 'Não definida'
                          }
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button 
                          onClick={(e) => {
                            e.stopPropagation()
                            router.push(`/dentista/pedidos/${pedido.id}`)
                          }}
                          className="text-blue-600 hover:text-blue-800 font-medium px-3 py-1 rounded-md hover:bg-blue-50"
                        >
                          Ver detalhes →
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}