// app/dentista/dashboard/page.tsx
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
  valorCobrado: number
  informacoesDetalhadas: string
  protetico: {
    nome: string
    id: number
    email: string
  }
  dentista: {
    nome: string
    id: number
  }
}

interface DashboardStats {
  totalPedidos: number
  emAndamento: number
  aguardando: number
  finalizados: number
  pedidosRecentes: Pedido[]
}

export default function DashboardDentista() {
  const router = useRouter()
  const [usuario, setUsuario] = useState<any>(null)
  const [stats, setStats] = useState<DashboardStats>({
    totalPedidos: 0,
    emAndamento: 0,
    aguardando: 0,
    finalizados: 0,
    pedidosRecentes: []
  })
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    carregarDashboard()
  }, [])

  const carregarDashboard = async () => {
    try {
      setCarregando(true)
      setErro('')
      
      // 1. Pega o usuário logado
      const usuarioJSON = localStorage.getItem('usuario')
      if (!usuarioJSON) {
        router.push('/login')
        return
      }
      
      const usuario = JSON.parse(usuarioJSON)
      setUsuario(usuario)
      console.log('🔍 Dentista logado - ID:', usuario.id, 'Nome:', usuario.nome)
      
      // 2. ✅ CORREÇÃO: Use o endpoint CORRETO que já existe no backend
      console.log(`🔄 Chamando: http://localhost:8080/api/pedidos/dentista/${usuario.id}`)
      
      const response = await fetch(`http://localhost:8080/api/pedidos/dentista/${usuario.id}`, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      })
    
      
      if (!response.ok) {
        const errorText = await response.text()
        console.error('❌ Erro na API:', response.status, errorText)
        throw new Error(`Erro ${response.status}: Não foi possível carregar os dados`)
      }
      
      const pedidosData = await response.json()
      console.log('✅ Dados recebidos:', pedidosData.length, 'pedidos')
      
      // 3. Verifica se é array
      if (!Array.isArray(pedidosData)) {
        throw new Error('Formato de dados inválido da API')
      }
      
      // 4. Mapeia para o formato esperado
      const pedidos: Pedido[] = pedidosData.map((item: any) => {
        // Garantir que protético existe
        const protetico = item.protetico || {}
        
        return {
          id: item.id || 0,
          codigo: item.codigo || `P${item.id}`,
          tipoServico: item.tipoServico || 'Não especificado',
          status: item.status || 'RASCUNHO',
          dataEntrada: item.dataEntrada || item.dataCriacao?.split('T')[0] || new Date().toISOString().split('T')[0],
          dataPrevistaEntrega: item.dataPrevistaEntrega || null,
          valorCobrado: item.valorCobrado || 0,
          informacoesDetalhadas: item.informacoesDetalhadas || '',
          protetico: {
            nome: protetico.nome || 'Protético não definido',
            id: protetico.id || 0,
            email: protetico.email || ''
          },
          dentista: {
            nome: item.dentista?.nome || usuario.nome,
            id: item.dentista?.id || usuario.id
          }
        }
      })
      
      // 5. Calcula estatísticas
      const totalPedidos = pedidos.length
      
      const emAndamento = pedidos.filter(pedido => 
        pedido.status === 'PRODUCAO' || 
        pedido.status === 'EM_ANDAMENTO' ||
        pedido.status === 'EM_PRODUCAO'
      ).length
      
      const aguardando = pedidos.filter(pedido => 
        pedido.status === 'RASCUNHO' || 
        pedido.status === 'AGUARDANDO' || 
        pedido.status === 'PENDENTE' ||
        pedido.status === 'AGUARDANDO_APROVACAO'
      ).length
      
      const finalizados = pedidos.filter(pedido => 
        pedido.status === 'ENTREGUE' || 
        pedido.status === 'FINALIZADO' || 
        pedido.status === 'CONCLUIDO' ||
        pedido.status === 'FINALIZADA'
      ).length
      
      // 6. Pega os 5 pedidos mais recentes
      const pedidosRecentes = [...pedidos]
        .sort((a, b) => {
          const dateA = a.dataEntrada ? new Date(a.dataEntrada).getTime() : 0
          const dateB = b.dataEntrada ? new Date(b.dataEntrada).getTime() : 0
          return dateB - dateA
        })
        .slice(0, 5)
      
      setStats({
        totalPedidos,
        emAndamento,
        aguardando,
        finalizados,
        pedidosRecentes
      })
      
      console.log(`📊 Dashboard carregado: ${totalPedidos} pedidos totais`)
      
    } catch (error: any) {
      console.error('💥 Erro ao carregar dashboard:', error)
      setErro(error.message || 'Erro ao carregar dados do dashboard')
    } finally {
      setCarregando(false)
    }
  }

  const handleNovoPedido = () => {
    router.push('/dentista/pedidos/novo')
  }

  const handleVerPedidos = () => {
    router.push('/dentista/pedidos')
  }

  const getStatusStyle = (status: string) => {
    const statusLower = status.toLowerCase()
    
    if (statusLower.includes('entregue') || 
        statusLower.includes('finalizado') || 
        statusLower.includes('concluido')) {
      return 'bg-green-100 text-green-800'
    } else if (statusLower.includes('producao') || 
               statusLower.includes('andamento')) {
      return 'bg-blue-100 text-blue-800'
    } else if (statusLower.includes('aguardando') || 
               statusLower.includes('pendente') || 
               statusLower.includes('rascunho')) {
      return 'bg-yellow-100 text-yellow-800'
    } else {
      return 'bg-gray-100 text-gray-800'
    }
  }

  const formatarTipoServico = (tipo: string) => {
    return tipo
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, l => l.toUpperCase())
  }

  const formatarData = (dataString: string) => {
    if (!dataString) return 'Não definida'
    try {
      return new Date(dataString).toLocaleDateString('pt-BR')
    } catch {
      return dataString
    }
  }

  const formatarValor = (valor: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor)
  }

  const recarregarDashboard = () => {
    carregarDashboard()
  }

  if (carregando) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-cyan-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="mt-4 text-gray-600">Carregando dashboard...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-cyan-50">
      {/* Header */}
      <header className="bg-white shadow-lg border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-xl flex items-center justify-center shadow-md">
                <span className="text-white font-bold text-sm">PL</span>
              </div>
              <div>
                <h1 className="text-xl font-bold text-gray-900">ProtéticoLab</h1>
                <p className="text-sm text-blue-600 font-medium">Área do Dentista</p>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              {usuario && (
                <div className="text-right">
                  <p className="text-sm font-medium text-gray-900">Olá, {usuario.nome}</p>
                  <p className="text-xs text-gray-500">{usuario.email}</p>
                  {usuario.cro && (
                    <p className="text-xs text-gray-500">CRO: {usuario.cro}</p>
                  )}
                </div>
              )}
              <button
                onClick={() => {
                  localStorage.removeItem('usuario')
                  router.push('/')
                }}
                className="bg-gradient-to-r from-red-500 to-red-600 text-white px-4 py-2 rounded-lg hover:from-red-600 hover:to-red-700 transition-all duration-200 shadow-md"
              >
                Sair
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Conteúdo Principal */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {erro && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex justify-between items-center">
            <div className="flex items-center">
              <span className="text-red-500 mr-2">⚠️</span>
              <p className="text-red-700">{erro}</p>
            </div>
            <button
              onClick={recarregarDashboard}
              className="text-sm bg-red-100 text-red-700 px-3 py-1 rounded hover:bg-red-200"
            >
              Tentar novamente
            </button>
          </div>
        )}

        {/* Estatísticas Rápidas */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-blue-100 rounded-xl">
                <span className="text-2xl">📋</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Meus Pedidos</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalPedidos}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-green-100 rounded-xl">
                <span className="text-2xl">⚡</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Em Andamento</p>
                <p className="text-2xl font-bold text-gray-900">{stats.emAndamento}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-yellow-100 rounded-xl">
                <span className="text-2xl">⏳</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Aguardando</p>
                <p className="text-2xl font-bold text-gray-900">{stats.aguardando}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-purple-100 rounded-xl">
                <span className="text-2xl">✅</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Finalizados</p>
                <p className="text-2xl font-bold text-gray-900">{stats.finalizados}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Ações Principais */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* Card Meus Pedidos */}
          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
            <div className="text-center">
              <div className="w-16 h-16 bg-gradient-to-r from-blue-500 to-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-md">
                <span className="text-2xl text-white">📋</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Meus Pedidos</h3>
              <p className="text-gray-600 text-sm mb-4">Acompanhe todos os seus pedidos de prótese</p>
              <button 
                onClick={handleVerPedidos}
                className="w-full bg-gradient-to-r from-blue-500 to-blue-600 text-white py-2 px-4 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 shadow-md"
              >
                Ver Pedidos ({stats.totalPedidos})
              </button>
            </div>
          </div>

          {/* Card Novo Pedido */}
          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
            <div className="text-center">
              <div className="w-16 h-16 bg-gradient-to-r from-green-500 to-green-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-md">
                <span className="text-2xl text-white">🦷</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Novo Pedido</h3>
              <p className="text-gray-600 text-sm mb-4">Solicite uma nova prótese dentária</p>
              <button 
                onClick={handleNovoPedido}
                className="w-full bg-gradient-to-r from-green-500 to-green-600 text-white py-2 px-4 rounded-lg hover:from-green-600 hover:to-green-700 transition-all duration-200 shadow-md"
              >
                Fazer Pedido
              </button>
            </div>
          </div>

          {/* Card Perfil */}
          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
            <div className="text-center">
              <div className="w-16 h-16 bg-gradient-to-r from-gray-500 to-gray-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-md">
                <span className="text-2xl text-white">👤</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Meu Perfil</h3>
              <p className="text-gray-600 text-sm mb-4">Atualize seus dados e configurações</p>
              <button 
                onClick={() => router.push('/dentista/perfil')}
                className="w-full bg-gradient-to-r from-gray-500 to-gray-600 text-white py-2 px-4 rounded-lg hover:from-gray-600 hover:to-gray-700 transition-all duration-200 shadow-md"
              >
                Editar Perfil
              </button>
            </div>
          </div>
        </div>

        {/* Seção de Pedidos Recentes */}
        <div className="mt-8 bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg font-semibold text-gray-800">📦 Pedidos Recentes</h3>
            <button 
              onClick={recarregarDashboard}
              className="text-sm text-blue-600 hover:text-blue-800 flex items-center gap-1 px-3 py-1 rounded hover:bg-blue-50"
            >
              <span className="text-lg">↻</span> Atualizar
            </button>
          </div>
          
          {stats.pedidosRecentes.length === 0 ? (
            <div className="text-center py-8">
              <div className="text-6xl mb-4">📭</div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Nenhum pedido encontrado</h3>
              <p className="text-gray-600 mb-6">Você ainda não tem pedidos cadastrados</p>
              <button 
                onClick={handleNovoPedido}
                className="text-blue-600 hover:text-blue-800 font-medium underline"
              >
                Faça seu primeiro pedido
              </button>
            </div>
          ) : (
            <>
              <div className="space-y-3">
                {stats.pedidosRecentes.map((pedido) => (
                  <div 
                    key={pedido.id} 
                    className="flex items-center justify-between p-4 bg-blue-50 rounded-xl hover:bg-blue-100 transition-colors cursor-pointer"
                    onClick={() => router.push(`/dentista/pedidos/${pedido.id}`)}
                  >
                    <div className="flex items-center">
                      <span className="text-blue-500 mr-4 text-2xl">🦷</span>
                      <div>
                        <div className="flex items-center gap-3 mb-1">
                          <span className="font-semibold text-gray-900">
                            {formatarTipoServico(pedido.tipoServico)} - #{pedido.codigo}
                          </span>
                          <span className={`text-xs px-2 py-1 rounded-full ${getStatusStyle(pedido.status)}`}>
                            {pedido.status.replace(/_/g, ' ')}
                          </span>
                        </div>
                        <div className="flex items-center gap-4 text-sm text-gray-600">
                          <span>Protético: {pedido.protetico.nome}</span>
                          <span>• Entrada: {formatarData(pedido.dataEntrada)}</span>
                          {pedido.dataPrevistaEntrega && (
                            <span>• Previsão: {formatarData(pedido.dataPrevistaEntrega)}</span>
                          )}
                          {pedido.valorCobrado > 0 && (
                            <span>• Valor: {formatarValor(pedido.valorCobrado)}</span>
                          )}
                        </div>
                      </div>
                    </div>
                    <button 
                      onClick={(e) => {
                        e.stopPropagation()
                        router.push(`/dentista/pedidos/${pedido.id}`)
                      }}
                      className="text-blue-600 hover:text-blue-800 font-medium text-sm px-3 py-1 rounded hover:bg-blue-200 bg-blue-100"
                    >
                      Detalhes →
                    </button>
                  </div>
                ))}
              </div>
              
              {stats.totalPedidos > 5 && (
                <div className="mt-6 text-center">
                  <button 
                    onClick={handleVerPedidos}
                    className="text-blue-600 hover:text-blue-800 font-medium text-sm"
                  >
                    Ver todos os {stats.totalPedidos} pedidos →
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </main>
    </div>
  )
}