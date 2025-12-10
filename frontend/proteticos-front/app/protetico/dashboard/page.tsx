// app/protetico/dashboard/page.tsx
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
  dentista: {
    nome: string
    id: number
    cro?: string
    especialidade?: string
  }
}

interface DashboardStats {
  pedidosRecebidos: number
  emProducao: number
  terceirizados: number
  finalizados: number
  pedidosRecentes: Pedido[]
  pedidosPrioritarios: Pedido[]
}

export default function DashboardProtetico() {
  const router = useRouter()
  const [usuario, setUsuario] = useState<any>(null)
  const [stats, setStats] = useState<DashboardStats>({
    pedidosRecebidos: 0,
    emProducao: 0,
    terceirizados: 0,
    finalizados: 0,
    pedidosRecentes: [],
    pedidosPrioritarios: []
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
      console.log('🔍 Protético logado - ID:', usuario.id, 'Nome:', usuario.nome)
      
      // 2. Usa o endpoint específico para protético
      console.log(`🔄 Chamando: http://localhost:8080/api/pedidos/para-frontend-protetico/${usuario.id}`)
      
      const response = await fetch(`http://localhost:8080/api/pedidos/para-frontend-protetico/${usuario.id}`, {
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
        // Garantir que dentista existe
        const dentista = item.dentista || {}
        
        return {
          id: item.id || 0,
          codigo: item.codigo || `P${item.id}`,
          tipoServico: item.tipoServico || 'Não especificado',
          status: item.status || 'RASCUNHO',
          dataEntrada: item.dataEntrada || item.dataCriacao?.split('T')[0] || new Date().toISOString().split('T')[0],
          dataPrevistaEntrega: item.dataPrevistaEntrega || null,
          valorCobrado: item.valorCobrado || 0,
          informacoesDetalhadas: item.informacoesDetalhadas || '',
          dentista: {
            nome: dentista.nome || 'Dentista não definido',
            id: dentista.id || 0,
            cro: dentista.cro || '',
            especialidade: dentista.especialidade || ''
          }
        }
      })
      
      // 5. Calcula estatísticas
      const pedidosRecebidos = pedidos.length
      
      const emProducao = pedidos.filter(pedido => 
        pedido.status === 'PRODUCAO' || 
        pedido.status === 'EM_ANDAMENTO' ||
        pedido.status === 'EM_PRODUCAO'
      ).length
      
      // Aqui você precisa adicionar a lógica para terceirizados
      // Por enquanto, vamos usar 0 ou buscar de outra API
      const terceirizados = 0 // TODO: Integrar com API de terceirização
      
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
        .slice(0, 4)
      
      // 7. Pedeos prioritários (atrasados ou com prazo próximo)
      const hoje = new Date()
      const pedidosPrioritarios = pedidos
        .filter(pedido => {
          if (!pedido.dataPrevistaEntrega) return false
          
          const prazo = new Date(pedido.dataPrevistaEntrega)
          const diferencaDias = Math.floor((prazo.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24))
          
          // Prioridade: atrasados ou com prazo em 2 dias
          return diferencaDias <= 2 && 
                 pedido.status !== 'ENTREGUE' && 
                 pedido.status !== 'FINALIZADO' &&
                 pedido.status !== 'CANCELADO'
        })
        .slice(0, 2)
      
      setStats({
        pedidosRecebidos,
        emProducao,
        terceirizados,
        finalizados,
        pedidosRecentes,
        pedidosPrioritarios
      })
      
      console.log(`📊 Dashboard carregado: ${pedidosRecebidos} pedidos totais`)
      
    } catch (error: any) {
      console.error('💥 Erro ao carregar dashboard:', error)
      setErro(error.message || 'Erro ao carregar dados do dashboard')
    } finally {
      setCarregando(false)
    }
  }

  // Funções de navegação
  const handleVerPedidos = () => {
    router.push('/protetico/pedidos')
  }

  const handleGerenciarConvites = () => {
    router.push('/convites') // Vai para a página de convites geral
  }

  const handleTerceirizar = () => {
    router.push('/protetico/terceirizacao') // Supondo que exista esta página
  }

  const handleEditarPerfil = () => {
    router.push('/protetico/perfil') // Supondo que exista esta página
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
    if (!valor || valor === 0) return 'Não definido'
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor)
  }

  const getStatusColor = (status: string) => {
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
    } else if (statusLower.includes('cancelado')) {
      return 'bg-red-100 text-red-800'
    } else {
      return 'bg-gray-100 text-gray-800'
    }
  }

  const calcularDiasRestantes = (dataPrevista: string) => {
    if (!dataPrevista) return null
    
    try {
      const hoje = new Date()
      const prazo = new Date(dataPrevista)
      const diferencaMs = prazo.getTime() - hoje.getTime()
      const diferencaDias = Math.ceil(diferencaMs / (1000 * 60 * 60 * 24))
      
      return diferencaDias
    } catch {
      return null
    }
  }

  const getPrioridadeTexto = (dias: number) => {
    if (dias < 0) return `Atrasado há ${Math.abs(dias)} dias`
    if (dias === 0) return 'Entrega hoje!'
    if (dias === 1) return 'Entrega amanhã'
    return `${dias} dias restantes`
  }

  const getPrioridadeCor = (dias: number) => {
    if (dias < 0) return 'bg-red-100 text-red-700 border-red-200'
    if (dias === 0) return 'bg-orange-100 text-orange-700 border-orange-200'
    if (dias <= 2) return 'bg-yellow-100 text-yellow-700 border-yellow-200'
    return 'bg-blue-100 text-blue-700 border-blue-200'
  }

  const recarregarDashboard = () => {
    carregarDashboard()
  }

  if (carregando) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 to-teal-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-green-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="mt-4 text-gray-600">Carregando dashboard...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 to-teal-50">
      {/* Header */}
      <header className="bg-white shadow-lg border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-r from-green-500 to-teal-500 rounded-xl flex items-center justify-center shadow-md">
                <span className="text-white font-bold text-sm">PL</span>
              </div>
              <div>
                <h1 className="text-xl font-bold text-gray-900">ProtéticoLab</h1>
                <p className="text-sm text-green-600 font-medium">Área do Protético</p>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              {usuario && (
                <div className="text-right">
                  <p className="text-sm font-medium text-gray-900">Olá, {usuario.nome}</p>
                  <p className="text-xs text-gray-500">{usuario.email}</p>
                  {usuario.especializacao && (
                    <p className="text-xs text-gray-500">Especialização: {usuario.especializacao}</p>
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
                <span className="text-2xl">📥</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Pedidos Recebidos</p>
                <p className="text-2xl font-bold text-gray-900">{stats.pedidosRecebidos}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-green-100 rounded-xl">
                <span className="text-2xl">⚡</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Em Produção</p>
                <p className="text-2xl font-bold text-gray-900">{stats.emProducao}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-purple-100 rounded-xl">
                <span className="text-2xl">🔄</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Terceirizados</p>
                <p className="text-2xl font-bold text-gray-900">{stats.terceirizados}</p>
                <p className="text-xs text-gray-500 mt-1">*Em breve</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-yellow-100 rounded-xl">
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
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {/* Card Pedidos */}
          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
            <div className="text-center">
              <div className="w-16 h-16 bg-gradient-to-r from-blue-500 to-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-md">
                <span className="text-2xl text-white">📥</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Pedidos Recebidos</h3>
              <p className="text-gray-600 text-sm mb-4">Veja e gerencie os pedidos dos dentistas</p>
              <button 
                onClick={handleVerPedidos}
                className="w-full bg-gradient-to-r from-blue-500 to-blue-600 text-white py-2 px-4 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 shadow-md"
              >
                Ver Pedidos ({stats.pedidosRecebidos})
              </button>
            </div>
          </div>

          {/* Card Convites */}
          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
            <div className="text-center">
              <div className="w-16 h-16 bg-gradient-to-r from-green-500 to-green-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-md">
                <span className="text-2xl text-white">📨</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Gerenciar Convites</h3>
              <p className="text-gray-600 text-sm mb-4">Convide dentistas e outros protéticos</p>
              <button 
                onClick={handleGerenciarConvites}
                className="w-full bg-gradient-to-r from-green-500 to-green-600 text-white py-2 px-4 rounded-lg hover:from-green-600 hover:to-green-700 transition-all duration-200 shadow-md"
              >
                Gerenciar Convites
              </button>
            </div>
          </div>

          {/* Card Terceirização */}
          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
            <div className="text-center">
              <div className="w-16 h-16 bg-gradient-to-r from-purple-500 to-purple-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-md">
                <span className="text-2xl text-white">🔄</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Terceirização</h3>
              <p className="text-gray-600 text-sm mb-4">Terceirize trabalhos para outros protéticos</p>
              <button 
                onClick={handleTerceirizar}
                className="w-full bg-gradient-to-r from-purple-500 to-purple-600 text-white py-2 px-4 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 shadow-md"
              >
                Terceirizar
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
                onClick={handleEditarPerfil}
                className="w-full bg-gradient-to-r from-gray-500 to-gray-600 text-white py-2 px-4 rounded-lg hover:from-gray-600 hover:to-gray-700 transition-all duration-200 shadow-md"
              >
                Editar Perfil
              </button>
            </div>
          </div>
        </div>

        {/* Duas colunas: Atividades Recentes e Pedidos Prioritários */}
        <div className="mt-8 grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Seção de Atividades Recentes */}
          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold text-gray-900">📋 Pedidos Recentes</h3>
              <button 
                onClick={handleVerPedidos}
                className="text-sm text-blue-600 hover:text-blue-800"
              >
                Ver todos
              </button>
            </div>
            
            {stats.pedidosRecentes.length === 0 ? (
              <div className="text-center py-8">
                <div className="text-6xl mb-4">📭</div>
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Nenhum pedido recente</h3>
                <p className="text-gray-600">Os dentistas ainda não enviaram pedidos para você</p>
              </div>
            ) : (
              <div className="space-y-3">
                {stats.pedidosRecentes.map((pedido) => (
                  <div 
                    key={pedido.id} 
                    className="flex items-center justify-between p-3 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors cursor-pointer"
                    onClick={() => router.push(`/protetico/pedidos/${pedido.id}`)}
                  >
                    <div className="flex items-center">
                      <span className="text-blue-500 mr-3">🦷</span>
                      <div>
                        <div className="flex items-center gap-2 mb-1">
                          <span className="text-sm font-medium">
                            #{pedido.codigo} - {pedido.dentista.nome}
                          </span>
                          <span className={`text-xs px-2 py-1 rounded-full ${getStatusColor(pedido.status)}`}>
                            {pedido.status.replace(/_/g, ' ')}
                          </span>
                        </div>
                        <p className="text-xs text-gray-600">
                          {formatarTipoServico(pedido.tipoServico)}
                          {pedido.dataPrevistaEntrega && ` • Prazo: ${formatarData(pedido.dataPrevistaEntrega)}`}
                        </p>
                      </div>
                    </div>
                    <span className="text-xs text-gray-500">
                      {formatarData(pedido.dataEntrada)}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Seção de Pedidos Prioritários */}
          <div className="bg-gradient-to-r from-red-50 to-orange-50 rounded-2xl p-6 shadow-lg border border-red-100">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <span className="text-red-500 text-xl mr-2">🚨</span>
                <h3 className="text-lg font-semibold text-red-900">Pedidos Prioritários</h3>
              </div>
              <span className="text-xs bg-red-100 text-red-700 px-2 py-1 rounded-full">
                {stats.pedidosPrioritarios.length} urgente(s)
              </span>
            </div>
            
            {stats.pedidosPrioritarios.length === 0 ? (
              <div className="text-center py-8">
                <div className="text-6xl mb-4">🎉</div>
                <h3 className="text-lg font-semibold text-red-900 mb-2">Nenhum pedido urgente</h3>
                <p className="text-red-700">Todos os pedidos estão dentro do prazo!</p>
              </div>
            ) : (
              <div className="space-y-3">
                {stats.pedidosPrioritarios.map((pedido) => {
                  const diasRestantes = calcularDiasRestantes(pedido.dataPrevistaEntrega)
                  
                  return (
                    <div 
                      key={pedido.id} 
                      className="bg-white rounded-lg border border-red-200 p-4 hover:shadow-md transition-shadow cursor-pointer"
                      onClick={() => router.push(`/protetico/pedidos/${pedido.id}`)}
                    >
                      <div className="flex justify-between items-start mb-2">
                        <div>
                          <div className="flex items-center gap-2 mb-1">
                            <span className="font-semibold text-gray-900">
                              #{pedido.codigo} - {formatarTipoServico(pedido.tipoServico)}
                            </span>
                          </div>
                          <p className="text-sm text-gray-600 mb-2">
                            Dentista: {pedido.dentista.nome}
                          </p>
                        </div>
                        {diasRestantes !== null && (
                          <span className={`text-xs px-3 py-1 rounded-full ${getPrioridadeCor(diasRestantes)} font-medium`}>
                            {getPrioridadeTexto(diasRestantes)}
                          </span>
                        )}
                      </div>
                      
                      <div className="flex justify-between items-center">
                        <span className="text-sm text-gray-700">
                          Valor: {formatarValor(pedido.valorCobrado)}
                        </span>
                        <button 
                          onClick={(e) => {
                            e.stopPropagation()
                            router.push(`/protetico/pedidos/${pedido.id}`)
                          }}
                          className="text-sm bg-red-100 text-red-700 px-3 py-1 rounded hover:bg-red-200 font-medium"
                        >
                          Ver detalhes →
                        </button>
                      </div>
                    </div>
                  )
                })}
              </div>
            )}
            
            {/* Dica para pedidos sem urgência */}
            {stats.pedidosPrioritarios.length === 0 && stats.pedidosRecebidos > 0 && (
              <div className="mt-4 text-center">
                <p className="text-sm text-green-700">
                  🎉 Você tem {stats.pedidosRecebidos} pedidos em andamento, todos dentro do prazo!
                </p>
              </div>
            )}
          </div>
        </div>

        {/* Botão de recarregar */}
        <div className="mt-6 text-center">
          <button
            onClick={recarregarDashboard}
            className="inline-flex items-center gap-2 px-4 py-2 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Atualizar Dashboard
          </button>
        </div>
      </main>
    </div>
  )
}