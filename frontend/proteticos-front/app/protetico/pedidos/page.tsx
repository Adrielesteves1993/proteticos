// app/protetico/pedidos/page.tsx - VERSÃO CORRIGIDA
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
  dentista: {
    nome: string
  }
  valorCobrado: number
}

export default function PedidosProtetico() {
  const router = useRouter()
  const [pedidos, setPedidos] = useState<Pedido[]>([])
  const [carregando, setCarregando] = useState(true)
  const [filtro, setFiltro] = useState('TODOS')
  const [erro, setErro] = useState('')

  useEffect(() => {
    carregarPedidos()
  }, [filtro])

  const carregarPedidos = async () => {
    try {
      setCarregando(true)
      setErro('')
      
      // 1. Pega o usuário logado do localStorage
      const usuarioJSON = localStorage.getItem('usuario')
      if (!usuarioJSON) {
        setErro('Usuário não encontrado. Faça login novamente.')
        setCarregando(false)
        return
      }
      
      const usuario = JSON.parse(usuarioJSON)
      console.log('🔍 Protético logado - ID:', usuario.id, 'Nome:', usuario.nome)
      console.log('🎯 Tipo do usuário (original):', usuario.tipo)
      
      // 2. VERIFICAÇÃO CORRIGIDA: Normaliza para maiúsculas antes de comparar
      const tipoUsuario = String(usuario.tipo || '').toUpperCase()
      console.log('🎯 Tipo normalizado:', tipoUsuario)
      
      if (!tipoUsuario.includes('PROTETICO')) {
        setErro('Apenas protéticos podem acessar esta página.')
        console.warn('⚠️ Tipo de usuário não autorizado:', usuario.tipo)
        setCarregando(false)
        return
      }
      
      // 3. Usa o endpoint específico para protético
      console.log(`🔄 Chamando: http://localhost:8080/api/pedidos/protetico/${usuario.id}`)
      
      const response = await fetch(`http://localhost:8080/api/pedidos/protetico/${usuario.id}`, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      })
      
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
      
      // 5. Mapeia para o formato esperado
      const pedidosFormatados = pedidosData.map((item: any): Pedido => {
        // Extrair nome do dentista
        let nomeDentista = 'Dentista não informado'
        if (item.dentista) {
          if (typeof item.dentista === 'object' && item.dentista.nome) {
            nomeDentista = item.dentista.nome
          } else if (typeof item.dentista === 'string') {
            nomeDentista = item.dentista
          }
        }
        
        // Extrair tipo de serviço
        let tipoServicoStr = 'Não especificado'
        if (item.tipoServico) {
          tipoServicoStr = typeof item.tipoServico === 'object' 
            ? item.tipoServico.name || item.tipoServico.toString()
            : item.tipoServico
        }
        
        return {
          id: item.id || 0,
          codigo: item.codigo || `P${item.id}`,
          tipoServico: tipoServicoStr,
          status: item.status || 'RASCUNHO',
          dataEntrada: item.dataEntrada || item.dataCriacao?.split('T')[0] || new Date().toISOString().split('T')[0],
          dataPrevistaEntrega: item.dataPrevistaEntrega || item.previsaoEntrega,
          dentista: {
            nome: nomeDentista
          },
          valorCobrado: item.valorCobrado || item.valor_cobrado || 0
        }
      })
      
      // 6. Aplica filtro de status
      let pedidosFiltrados = pedidosFormatados
      if (filtro !== 'TODOS') {
        pedidosFiltrados = pedidosFormatados.filter(pedido => 
          pedido.status === filtro
        )
      }
      
      // 7. Ordena por ID (mais recente primeiro)
      pedidosFiltrados.sort((a, b) => b.id - a.id)
      
      console.log(`📊 ${pedidosFiltrados.length} pedidos carregados com sucesso`)
      setPedidos(pedidosFiltrados)
      
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
      case 'FINALIZADO': return 'bg-green-100 text-green-800 border border-green-200'
      case 'ENTREGUE': return 'bg-green-50 text-green-700 border border-green-300'
      case 'APROVADO': return 'bg-blue-100 text-blue-800 border border-blue-200'
      case 'EM_PRODUCAO': return 'bg-purple-100 text-purple-800 border border-purple-200'
      case 'AGUARDANDO_APROVACAO': return 'bg-yellow-100 text-yellow-800 border border-yellow-200'
      case 'RASCUNHO': return 'bg-gray-100 text-gray-800 border border-gray-200'
      case 'CANCELADO': return 'bg-red-100 text-red-800 border border-red-200'
      default: return 'bg-gray-100 text-gray-800 border border-gray-200'
    }
  }

  const formatarValor = (valor: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor)
  }

  const formatarData = (dataString: string) => {
    try {
      return new Date(dataString).toLocaleDateString('pt-BR')
    } catch {
      return 'Data inválida'
    }
  }

  const recarregarPedidos = () => {
    carregarPedidos()
  }

  const formatarTipoServico = (tipo: string) => {
    return tipo
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, l => l.toUpperCase())
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Pedidos Recebidos</h1>
            <p className="text-gray-600">Gerencie os pedidos dos dentistas</p>
          </div>
          <button
            onClick={recarregarPedidos}
            className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors flex items-center gap-2"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Atualizar
          </button>
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
              </div>
            </div>
          </div>
        )}

        {/* Filtros */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-4 mb-6">
          <div className="flex flex-wrap gap-2">
            {[
              'TODOS', 
              'AGUARDANDO_APROVACAO',
              'APROVADO',
              'EM_PRODUCAO',
              'FINALIZADO',
              'CANCELADO'
            ].map((filtroItem) => (
              <button
                key={filtroItem}
                onClick={() => setFiltro(filtroItem)}
                className={`px-4 py-2 rounded-lg transition-colors font-medium ${
                  filtro === filtroItem
                    ? 'bg-green-600 text-white shadow-sm'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                {filtroItem.replace(/_/g, ' ')}
              </button>
            ))}
          </div>
        </div>

        {/* Lista de Pedidos */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
          {carregando ? (
            <div className="p-12 text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto mb-4"></div>
              <p className="text-gray-600 font-medium">Carregando pedidos da API...</p>
              <p className="text-sm text-gray-500 mt-1">Buscando seus pedidos no sistema</p>
            </div>
          ) : pedidos.length === 0 ? (
            <div className="p-12 text-center">
              <div className="text-6xl mb-4">📭</div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Nenhum pedido recebido</h3>
              <p className="text-gray-600 mb-6">Os pedidos dos dentistas aparecerão aqui quando forem criados</p>
              <div className="space-y-2 text-sm text-gray-500 max-w-md mx-auto">
                <p>• Verifique se você está logado como protético</p>
                <p>• Dentistas precisam criar pedidos e associar a você</p>
                <p>• Tente atualizar a página ou verificar o console para detalhes</p>
              </div>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Código
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Dentista
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Tipo
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Valor
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
                      onClick={() => router.push(`/protetico/pedidos/${pedido.id}`)}
                    >
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-semibold text-gray-900">{pedido.codigo}</div>
                        <div className="text-xs text-gray-500">
                          {formatarData(pedido.dataEntrada)}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900 font-medium">{pedido.dentista.nome}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">{formatarTipoServico(pedido.tipoServico)}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-semibold text-gray-900">
                          {pedido.valorCobrado > 0 ? formatarValor(pedido.valorCobrado) : 'Não definido'}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex px-3 py-1 text-xs font-semibold rounded-full ${getStatusColor(pedido.status)}`}>
                          {pedido.status.replace(/_/g, ' ')}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          {pedido.dataPrevistaEntrega 
                            ? formatarData(pedido.dataPrevistaEntrega)
                            : 'Não definida'
                          }
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button 
                          onClick={(e) => {
                            e.stopPropagation()
                            router.push(`/protetico/pedidos/${pedido.id}`)
                          }}
                          className="text-green-600 hover:text-green-800 font-medium px-3 py-1 rounded-md hover:bg-green-50"
                        >
                          Ver detalhes →
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              
              {/* Contador */}
              <div className="px-6 py-3 bg-gray-50 border-t border-gray-200">
                <div className="text-sm text-gray-500">
                  Mostrando <span className="font-semibold">{pedidos.length}</span> pedido(s)
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}