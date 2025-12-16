"use client"

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { 
  ProteticoSimplesDTO, 
  TerceirizacaoResponseDTO, 
  SolicitarTerceirizacaoRequest,
  PedidoParaTerceirizacao 
} from '@/interfaces/terceirizacao'

export default function TerceirizacaoProtetico() {
  const router = useRouter()
  const [pedidosDisponiveis, setPedidosDisponiveis] = useState<PedidoParaTerceirizacao[]>([])
  const [terceirizacoes, setTerceirizacoes] = useState<TerceirizacaoResponseDTO[]>([])
  const [proteticosDisponiveis, setProteticosDisponiveis] = useState<ProteticoSimplesDTO[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState<string | null>(null)
  const [abaAtiva, setAbaAtiva] = useState<'disponiveis' | 'minhas' | 'recebidas'>('disponiveis')
  const [modalAberto, setModalAberto] = useState(false)
  const [pedidoSelecionado, setPedidoSelecionado] = useState<PedidoParaTerceirizacao | null>(null)
  const [proteticoSelecionado, setProteticoSelecionado] = useState<string>('')
  const [percentual, setPercentual] = useState<string>('50') // Percentual padrão 50%
  const [observacoes, setObservacoes] = useState('')
  const [usuario, setUsuario] = useState<any>(null)

  useEffect(() => {
    carregarDados()
  }, [])

  const carregarDados = async () => {
    try {
      setCarregando(true)
      setErro(null)
      
      // Carrega usuário
      const usuarioJSON = localStorage.getItem('usuario')
      if (!usuarioJSON) {
        router.push('/login')
        return
      }
      
      const usuarioData = JSON.parse(usuarioJSON)
      setUsuario(usuarioData)
      
      // Carrega dados em paralelo
      await Promise.all([
        carregarPedidosDisponiveis(usuarioData.id),
        carregarMinhasTerceirizacoes(usuarioData.id)
      ])
      
    } catch (error: any) {
      console.error('Erro ao carregar dados:', error)
      setErro(error.message || 'Erro ao carregar dados')
    } finally {
      setCarregando(false)
    }
  }

  const carregarPedidosDisponiveis = async (proteticoId: number) => {
    try {
      // Busca pedidos do protético que podem ser terceirizados
      const response = await fetch(`http://localhost:8080/api/pedidos/protetico/${proteticoId}`)
      
      if (!response.ok) {
        throw new Error(`Erro ${response.status} ao carregar pedidos`)
      }
      
      const pedidosData = await response.json()
      
      if (!Array.isArray(pedidosData)) {
        throw new Error('Formato de dados inválido')
      }
      
      // Filtra apenas pedidos em produção que podem ser terceirizados
      const pedidosTerceirizaveis = pedidosData
        .filter((item: any) => {
          const status = item.status || ''
          return status === 'EM_PRODUCAO' || 
                 status === 'PRODUCAO' || 
                 status === 'EM_ANDAMENTO'
        })
        .map((item: any): PedidoParaTerceirizacao => {
          const dentista = item.dentista || {}
          const protetico = item.protetico || {}
          
          return {
            id: item.id || 0,
            codigo: item.codigo || `P${item.id}`,
            tipoServico: item.tipoServico || 'Não especificado',
            status: item.status || 'RASCUNHO',
            dataEntrada: item.dataEntrada || new Date().toISOString().split('T')[0],
            dataPrevistaEntrega: item.dataPrevistaEntrega,
            valorCobrado: item.valorCobrado || 0,
            informacoesDetalhadas: item.informacoesDetalhadas || '',
            dentista: {
              nome: dentista.nome || 'Dentista não definido',
              id: dentista.id || 0
            },
            protetico: {
              id: protetico.id || proteticoId,
              nome: protetico.nome || usuario?.nome || 'Protético'
            }
          }
        })
      
      setPedidosDisponiveis(pedidosTerceirizaveis)
      
    } catch (error) {
      console.error('Erro ao carregar pedidos disponíveis:', error)
      setPedidosDisponiveis([])
    }
  }

  const carregarMinhasTerceirizacoes = async (proteticoId: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/terceirizacoes/protetico/${proteticoId}`)
      
      if (!response.ok) {
        throw new Error(`Erro ${response.status} ao carregar terceirizações`)
      }
      
      const data = await response.json()
      setTerceirizacoes(Array.isArray(data) ? data : [])
      
    } catch (error) {
      console.error('Erro ao carregar terceirizações:', error)
      setTerceirizacoes([])
    }
  }

  const carregarProteticosDisponiveisParaPedido = async (pedidoId: number, tipoServico: string) => {
    try {
      console.log(`🔄 Carregando protéticos para pedido ${pedidoId}, tipo: ${tipoServico}`)
      
      // Encode the tipoServico properly
      const encodedTipoServico = encodeURIComponent(tipoServico)
      const url = `http://localhost:8080/api/terceirizacoes/disponiveis?pedidoId=${pedidoId}&tipoServico=${encodedTipoServico}`
      
      console.log('📡 URL da requisição:', url)
      
      const response = await fetch(url, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      })
      
      console.log('📡 Status da resposta:', response.status)
      
      if (response.ok) {
        const data = await response.json()
        console.log('✅ Protéticos disponíveis recebidos:', data)
        
        // Verifica se o retorno tem sucesso: false (erro da API)
        if (data.success === false) {
          console.warn('⚠️ API retornou erro:', data.message)
          setProteticosDisponiveis([])
          return
        }
        
        setProteticosDisponiveis(Array.isArray(data) ? data : [])
      } else {
        const errorText = await response.text()
        console.error('❌ Erro na API:', errorText)
        
        // Tenta parsear como JSON se possível
        try {
          const errorJson = JSON.parse(errorText)
          if (errorJson.message) {
            console.warn('⚠️ Erro da API:', errorJson.message)
          }
        } catch {
          console.error('❌ Erro texto:', errorText)
        }
        
        setProteticosDisponiveis([])
      }
      
    } catch (error) {
      console.error('💥 Erro ao carregar protéticos disponíveis:', error)
      setProteticosDisponiveis([])
    }
  }

  const abrirModalTerceirizacao = async (pedido: PedidoParaTerceirizacao) => {
    setPedidoSelecionado(pedido)
    setObservacoes('')
    setProteticoSelecionado('')
    setPercentual('50')
    setModalAberto(true)
    
    // Carrega os protéticos disponíveis para este pedido
    await carregarProteticosDisponiveisParaPedido(pedido.id, pedido.tipoServico)
  }

  const fecharModal = () => {
    setModalAberto(false)
    setPedidoSelecionado(null)
    setProteticosDisponiveis([])
  }

  const handleSolicitarTerceirizacao = async () => {
    if (!pedidoSelecionado || !proteticoSelecionado) {
      alert('Selecione um protético para solicitar a terceirização')
      return
    }

    const percentualNumero = parseFloat(percentual)
    if (isNaN(percentualNumero) || percentualNumero <= 0 || percentualNumero > 100) {
      alert('Percentual deve ser um número entre 0.01 e 100')
      return
    }

    try {
      const requestBody: SolicitarTerceirizacaoRequest = {
        proteticoTerceirizadoId: parseInt(proteticoSelecionado),
        percentual: percentualNumero,
        motivo: observacoes || undefined
      }

      console.log('📤 Enviando solicitação:', {
        pedidoId: pedidoSelecionado.id,
        proteticoId: usuario.id,
        body: requestBody
      })

      const response = await fetch(
        `http://localhost:8080/api/terceirizacoes/pedido/${pedidoSelecionado.id}/solicitar?proteticoId=${usuario.id}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(requestBody)
        }
      )

      const responseData = await response.json()
      console.log('📥 Resposta da API:', responseData)

      if (response.ok && responseData.success !== false) {
        alert('Solicitação de terceirização enviada com sucesso!')
        fecharModal()
        carregarDados() // Recarrega todos os dados
      } else {
        throw new Error(responseData.message || 'Erro ao solicitar terceirização')
      }
    } catch (error: any) {
      console.error('Erro ao solicitar terceirização:', error)
      alert(`Erro: ${error.message}`)
    }
  }

  const handleAcaoTerceirizacao = async (pedidoId: number, acao: 'aceitar' | 'recusar' | 'iniciar' | 'concluir' | 'cancelar') => {
    if (!usuario?.id) {
      alert('Usuário não identificado')
      return
    }

    let mensagemConfirmacao = ''
    switch (acao) {
      case 'aceitar': mensagemConfirmacao = 'Tem certeza que deseja aceitar esta terceirização?'; break
      case 'recusar': mensagemConfirmacao = 'Tem certeza que deseja recusar esta terceirização?'; break
      case 'iniciar': mensagemConfirmacao = 'Tem certeza que deseja iniciar a execução?'; break
      case 'concluir': mensagemConfirmacao = 'Tem certeza que deseja concluir a terceirização?'; break
      case 'cancelar': mensagemConfirmacao = 'Tem certeza que deseja cancelar esta terceirização?'; break
    }

    if (!confirm(mensagemConfirmacao)) {
      return
    }

    try {
      console.log(`🔄 ${acao} terceirização do pedido ${pedidoId} para protético ${usuario.id}`)
      
      const url = `http://localhost:8080/api/terceirizacoes/pedido/${pedidoId}/${acao}?proteticoId=${usuario.id}`
      
      const response = await fetch(url, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        }
      })

      const responseData = await response.json()
      console.log(`📥 Resposta do ${acao}:`, responseData)

      if (response.ok && responseData.success !== false) {
        alert(`Terceirização ${acao} com sucesso!`)
        carregarDados()
      } else {
        throw new Error(responseData.message || `Erro ao ${acao} terceirização`)
      }
    } catch (error: any) {
      console.error(`Erro ao ${acao} terceirização:`, error)
      alert(`Erro: ${error.message}`)
    }
  }

  const getStatusColor = (status: string) => {
    const statusLower = status.toLowerCase()
    
    if (statusLower.includes('concluido') || statusLower.includes('aceito')) {
      return 'bg-green-100 text-green-800'
    } else if (statusLower.includes('pendente') || statusLower.includes('solicitado')) {
      return 'bg-yellow-100 text-yellow-800'
    } else if (statusLower.includes('recusado') || statusLower.includes('cancelado')) {
      return 'bg-red-100 text-red-800'
    } else if (statusLower.includes('andamento') || statusLower.includes('iniciado')) {
      return 'bg-blue-100 text-blue-800'
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
    if (!valor || valor === 0) return 'Não definido'
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor)
  }

  const recarregar = () => {
    carregarDados()
  }

  // Filtrar terceirizações
  const terceirizacoesEnviadas = terceirizacoes.filter(t => 
    t.proteticoSolicitante?.id === usuario?.id
  )
  
  const terceirizacoesRecebidas = terceirizacoes.filter(t => 
    t.proteticoExecutor?.id === usuario?.id
  )

  // Função para obter o texto do status
  const getStatusText = (status: string) => {
    const statusMap: Record<string, string> = {
      'SOLICITADO': 'Solicitado',
      'ACEITO': 'Aceito',
      'RECUSADO': 'Recusado',
      'EM_ANDAMENTO': 'Em Andamento',
      'CONCLUIDO': 'Concluído',
      'CANCELADO': 'Cancelado'
    }
    return statusMap[status] || status
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
                <p className="text-sm text-green-600 font-medium">Terceirização</p>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <button
                onClick={() => router.push('/protetico/dashboard')}
                className="text-gray-600 hover:text-gray-900 px-4 py-2 rounded-lg hover:bg-gray-100"
              >
                ← Voltar ao Dashboard
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
              onClick={recarregar}
              className="text-sm bg-red-100 text-red-700 px-3 py-1 rounded hover:bg-red-200"
            >
              Tentar novamente
            </button>
          </div>
        )}

        {/* Estatísticas Rápidas */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-blue-100 rounded-xl">
                <span className="text-2xl">📤</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Disponíveis</p>
                <p className="text-2xl font-bold text-gray-900">{pedidosDisponiveis.length}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-green-100 rounded-xl">
                <span className="text-2xl">📨</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Enviadas</p>
                <p className="text-2xl font-bold text-gray-900">{terceirizacoesEnviadas.length}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
            <div className="flex items-center">
              <div className="p-3 bg-purple-100 rounded-xl">
                <span className="text-2xl">📥</span>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Recebidas</p>
                <p className="text-2xl font-bold text-gray-900">{terceirizacoesRecebidas.length}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Abas */}
        <div className="mb-8">
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex space-x-8">
              <button
                onClick={() => setAbaAtiva('disponiveis')}
                className={`py-4 px-1 border-b-2 font-medium text-sm ${abaAtiva === 'disponiveis'
                    ? 'border-green-500 text-green-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
              >
                📤 Pedidos Disponíveis
              </button>
              <button
                onClick={() => setAbaAtiva('minhas')}
                className={`py-4 px-1 border-b-2 font-medium text-sm ${abaAtiva === 'minhas'
                    ? 'border-green-500 text-green-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
              >
                📨 Minhas Solicitações
              </button>
              <button
                onClick={() => setAbaAtiva('recebidas')}
                className={`py-4 px-1 border-b-2 font-medium text-sm ${abaAtiva === 'recebidas'
                    ? 'border-green-500 text-green-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
              >
                📥 Solicitações Recebidas
              </button>
            </nav>
          </div>
        </div>

        {/* Conteúdo das Abas */}
        {carregando ? (
          <div className="text-center py-12">
            <div className="w-16 h-16 border-4 border-green-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
            <p className="mt-4 text-gray-600">Carregando dados...</p>
          </div>
        ) : (
          <>
            {/* Aba: Pedidos Disponíveis */}
            {abaAtiva === 'disponiveis' && (
              <div className="bg-white rounded-2xl shadow-lg border border-gray-100">
                <div className="p-6 border-b">
                  <div className="flex justify-between items-center">
                    <h3 className="text-lg font-semibold text-gray-900">
                      Pedidos Disponíveis para Terceirização
                    </h3>
                    <button
                      onClick={recarregar}
                      className="text-sm text-green-600 hover:text-green-800 font-medium"
                    >
                      ↻ Atualizar
                    </button>
                  </div>
                  <p className="text-gray-600 text-sm mt-1">
                    Pedidos em produção que podem ser terceirizados para outros protéticos
                  </p>
                </div>

                {pedidosDisponiveis.length === 0 ? (
                  <div className="p-12 text-center">
                    <div className="text-6xl mb-4">📭</div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      Nenhum pedido disponível para terceirização
                    </h3>
                    <p className="text-gray-600">
                      Todos os seus pedidos já estão sendo processados ou não estão em produção.
                    </p>
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead className="bg-gray-50">
                        <tr>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Código
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Tipo
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Dentista
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Valor
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Previsão
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Ação
                          </th>
                        </tr>
                      </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
                        {pedidosDisponiveis.map((pedido) => (
                          <tr key={pedido.id} className="hover:bg-gray-50">
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm font-semibold text-gray-900">
                                #{pedido.codigo}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm text-gray-900">
                                {formatarTipoServico(pedido.tipoServico)}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm text-gray-900">{pedido.dentista.nome}</div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm font-medium text-green-600">
                                {formatarValor(pedido.valorCobrado)}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm text-gray-900">
                                {formatarData(pedido.dataPrevistaEntrega)}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <button
                                onClick={() => abrirModalTerceirizacao(pedido)}
                                className="bg-gradient-to-r from-purple-500 to-purple-600 text-white px-4 py-2 rounded-lg hover:from-purple-600 hover:to-purple-700 text-sm"
                              >
                                Terceirizar
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}

            {/* Aba: Minhas Solicitações */}
            {abaAtiva === 'minhas' && (
              <div className="bg-white rounded-2xl shadow-lg border border-gray-100">
                <div className="p-6 border-b">
                  <h3 className="text-lg font-semibold text-gray-900">
                    Minhas Solicitações de Terceirização
                  </h3>
                  <p className="text-gray-600 text-sm mt-1">
                    Solicitações que você enviou para outros protéticos
                  </p>
                </div>

                {terceirizacoesEnviadas.length === 0 ? (
                  <div className="p-12 text-center">
                    <div className="text-6xl mb-4">📤</div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      Nenhuma solicitação enviada
                    </h3>
                    <p className="text-gray-600">
                      Você ainda não enviou solicitações de terceirização.
                    </p>
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead className="bg-gray-50">
                        <tr>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Pedido
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Para Protético
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Status
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Percentual
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Data
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Ações
                          </th>
                        </tr>
                      </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
                        {terceirizacoesEnviadas.map((terc) => (
                          <tr key={terc.id} className="hover:bg-gray-50">
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm font-semibold text-gray-900">
                                #{terc.codigoPedido}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm text-gray-900">
                                {terc.proteticoExecutor?.nome || 'Não definido'}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <span className={`inline-flex px-3 py-1 text-xs font-semibold rounded-full ${getStatusColor(terc.status)}`}>
                                {getStatusText(terc.status)}
                              </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm text-gray-900">
                                {terc.percentualTerceirizado?.toFixed(1)}%
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm text-gray-900">
                                {formatarData(terc.dataSolicitacao)}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="flex space-x-2">
                                {terc.status === 'SOLICITADO' && (
                                  <button
                                    onClick={() => handleAcaoTerceirizacao(terc.pedidoId, 'cancelar')}
                                    className="text-sm bg-red-100 text-red-700 px-3 py-1 rounded hover:bg-red-200"
                                  >
                                    Cancelar
                                  </button>
                                )}
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}

            {/* Aba: Solicitações Recebidas */}
            {abaAtiva === 'recebidas' && (
              <div className="bg-white rounded-2xl shadow-lg border border-gray-100">
                <div className="p-6 border-b">
                  <h3 className="text-lg font-semibold text-gray-900">
                    Solicitações Recebidas
                  </h3>
                  <p className="text-gray-600 text-sm mt-1">
                    Solicitações de terceirização que você recebeu de outros protéticos
                  </p>
                </div>

                {terceirizacoesRecebidas.length === 0 ? (
                  <div className="p-12 text-center">
                    <div className="text-6xl mb-4">📥</div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      Nenhuma solicitação recebida
                    </h3>
                    <p className="text-gray-600">
                      Você ainda não recebeu solicitações de terceirização.
                    </p>
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead className="bg-gray-50">
                        <tr>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Pedido
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            De Protético
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Status
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Percentual
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Data
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                            Ações
                          </th>
                        </tr>
                      </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
                        {terceirizacoesRecebidas.map((terc) => (
                          <tr key={terc.id} className="hover:bg-gray-50">
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm font-semibold text-gray-900">
                                #{terc.codigoPedido}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm text-gray-900">
                                {terc.proteticoSolicitante?.nome || 'Não definido'}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <span className={`inline-flex px-3 py-1 text-xs font-semibold rounded-full ${getStatusColor(terc.status)}`}>
                                {getStatusText(terc.status)}
                              </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm text-gray-900">
                                {terc.percentualTerceirizado?.toFixed(1)}%
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="text-sm text-gray-900">
                                {formatarData(terc.dataSolicitacao)}
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="flex space-x-2">
                                {terc.status === 'SOLICITADO' && (
                                  <>
                                    <button
                                      onClick={() => handleAcaoTerceirizacao(terc.pedidoId, 'aceitar')}
                                      className="text-sm bg-green-100 text-green-700 px-3 py-1 rounded hover:bg-green-200"
                                    >
                                      Aceitar
                                    </button>
                                    <button
                                      onClick={() => handleAcaoTerceirizacao(terc.pedidoId, 'recusar')}
                                      className="text-sm bg-red-100 text-red-700 px-3 py-1 rounded hover:bg-red-200"
                                    >
                                      Recusar
                                    </button>
                                  </>
                                )}
                                {terc.status === 'ACEITO' && (
                                  <button
                                    onClick={() => handleAcaoTerceirizacao(terc.pedidoId, 'iniciar')}
                                    className="text-sm bg-blue-100 text-blue-700 px-3 py-1 rounded hover:bg-blue-200"
                                  >
                                    Iniciar
                                  </button>
                                )}
                                {terc.status === 'EM_ANDAMENTO' && (
                                  <button
                                    onClick={() => handleAcaoTerceirizacao(terc.pedidoId, 'concluir')}
                                    className="text-sm bg-green-100 text-green-700 px-3 py-1 rounded hover:bg-green-200"
                                  >
                                    Concluir
                                  </button>
                                )}
                                {(terc.status === 'ACEITO' || terc.status === 'EM_ANDAMENTO') && (
                                  <button
                                    onClick={() => handleAcaoTerceirizacao(terc.pedidoId, 'cancelar')}
                                    className="text-sm bg-red-100 text-red-700 px-3 py-1 rounded hover:bg-red-200"
                                  >
                                    Cancelar
                                  </button>
                                )}
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}
          </>
        )}

        {/* Botão de recarregar */}
        <div className="mt-8 text-center">
          <button
            onClick={recarregar}
            className="inline-flex items-center gap-2 px-5 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors shadow-md"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Atualizar Dados
          </button>
        </div>
      </main>

      {/* Modal de Terceirização */}
      {modalAberto && pedidoSelecionado && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md">
            <div className="p-6">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-semibold text-gray-900">
                  Solicitar Terceirização
                </h3>
                <button
                  onClick={fecharModal}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ✕
                </button>
              </div>
              
              <div className="mb-4 bg-blue-50 p-3 rounded-lg">
                <p className="text-sm text-gray-600 mb-1">Pedido:</p>
                <p className="font-medium">#{pedidoSelecionado.codigo} - {formatarTipoServico(pedidoSelecionado.tipoServico)}</p>
                <p className="text-sm text-gray-600">Dentista: {pedidoSelecionado.dentista.nome}</p>
                <p className="text-sm text-gray-600">Valor: {formatarValor(pedidoSelecionado.valorCobrado)}</p>
              </div>

              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Protético Terceirizado *
                </label>
                {proteticosDisponiveis.length === 0 ? (
                  <div className="text-sm text-yellow-600 bg-yellow-50 p-3 rounded-lg">
                    <p>❌ Nenhum protético disponível para este tipo de serviço</p>
                    <p className="text-xs mt-1">
                      Tipo de serviço: {formatarTipoServico(pedidoSelecionado.tipoServico)}
                    </p>
                  </div>
                ) : (
                  <select
                    value={proteticoSelecionado}
                    onChange={(e) => setProteticoSelecionado(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    required
                  >
                    <option value="">Selecione um protético</option>
                    {proteticosDisponiveis.map((protetico) => (
                      <option key={protetico.id} value={protetico.id}>
                        {protetico.nome} 
                        {protetico.especializacao && ` - ${protetico.especializacao}`}
                        {protetico.taxaMinimaTerceirizacao && ` (Mín: ${protetico.taxaMinimaTerceirizacao}%)`}
                      </option>
                    ))}
                  </select>
                )}
              </div>

              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Percentual da Terceirização (%) *
                </label>
                <input
                  type="number"
                  min="0.1"
                  max="100"
                  step="0.1"
                  value={percentual}
                  onChange={(e) => setPercentual(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  placeholder="Ex: 50"
                />
                <p className="text-xs text-gray-500 mt-1">
                  Percentual do valor total que será repassado ao protético terceirizado
                </p>
              </div>

              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Observações / Instruções
                </label>
                <textarea
                  value={observacoes}
                  onChange={(e) => setObservacoes(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  rows={3}
                  placeholder="Instruções especiais, prazos, detalhes técnicos..."
                />
              </div>

              <div className="flex justify-end space-x-3">
                <button
                  onClick={fecharModal}
                  className="px-4 py-2 text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg"
                >
                  Cancelar
                </button>
                <button
                  onClick={handleSolicitarTerceirizacao}
                  disabled={!proteticoSelecionado || proteticosDisponiveis.length === 0}
                  className={`px-4 py-2 rounded-lg ${
                    !proteticoSelecionado || proteticosDisponiveis.length === 0
                      ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                      : 'bg-gradient-to-r from-purple-500 to-purple-600 text-white hover:from-purple-600 hover:to-purple-700'
                  }`}
                >
                  Enviar Solicitação
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}