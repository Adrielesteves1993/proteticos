// app/protetico/pedidos/[id]/page.tsx
"use client"

import { useEffect, useState } from 'react'
import { useRouter, useParams } from 'next/navigation'

interface Pedido {
  id: number
  codigo: string
  tipoServico: string
  status: string
  dataEntrada: string
  dataPrevistaEntrega: string
  dataEntrega?: string
  valorCobrado: number
  informacoesDetalhadas: string
  dentista: {
    nome: string
    id: number
    cro?: string
    especialidade?: string
    telefone?: string
    email?: string
  }
}

interface Etapa {
  id: number
  nomeEtapa: string
  observacoes: string
  status: string
  ordem: number
  dataCriacao?: string
  dataConclusao?: string
  responsavel?: {
    id: number
    nome: string
  }
}

interface Usuario {
  id: number
  nome: string
  email: string
  tipo: 'PROTETICO' | 'DENTISTA' | 'ADMIN'
}

export default function DetalhesPedidoProtetico() {
  const router = useRouter()
  const params = useParams()
  const id = params.id as string
  
  const [pedido, setPedido] = useState<Pedido | null>(null)
  const [etapas, setEtapas] = useState<Etapa[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [usuario, setUsuario] = useState<Usuario | null>(null)
  
  // Estados para modais
  const [showAprovarModal, setShowAprovarModal] = useState(false)
  const [showStatusModal, setShowStatusModal] = useState(false)
  const [showValorModal, setShowValorModal] = useState(false)
  const [showPrazoModal, setShowPrazoModal] = useState(false)
  const [showEtapaModal, setShowEtapaModal] = useState(false)
  
  // Estados para formulários
  const [novoStatus, setNovoStatus] = useState('')
  const [novoValor, setNovoValor] = useState('')
  const [novoPrazo, setNovoPrazo] = useState('')
  const [observacoes, setObservacoes] = useState('')
  const [nomeEtapa, setNomeEtapa] = useState('')
  const [salvando, setSalvando] = useState(false)

  useEffect(() => {
    carregarDados()
  }, [id])

  const carregarDados = async () => {
    try {
      setCarregando(true)
      setErro('')
      
      // Carrega usuário
      const usuarioJSON = localStorage.getItem('usuario')
      if (!usuarioJSON) {
        router.push('/login')
        return
      }
      const usuarioData = JSON.parse(usuarioJSON)
      setUsuario(usuarioData)
      
      // Carrega pedido
      console.log(`🔄 Buscando pedido ${id}...`)
      const response = await fetch(`http://localhost:8080/api/pedidos/${id}`, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      })
      
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: Pedido não encontrado`)
      }
      
      const pedidoData = await response.json()
      console.log('✅ Pedido carregado:', pedidoData)
      
      // Formata pedido
      const pedidoFormatado: Pedido = {
        id: pedidoData.id,
        codigo: pedidoData.codigo || `P${pedidoData.id}`,
        tipoServico: pedidoData.tipoServico || 'Não especificado',
        status: pedidoData.status || 'RASCUNHO',
        dataEntrada: pedidoData.dataEntrada || pedidoData.dataCriacao?.split('T')[0] || new Date().toISOString().split('T')[0],
        dataPrevistaEntrega: pedidoData.dataPrevistaEntrega || pedidoData.dataPrevista || '',
        dataEntrega: pedidoData.dataEntrega,
        valorCobrado: pedidoData.valorCobrado || 0,
        informacoesDetalhadas: pedidoData.informacoesDetalhadas || pedidoData.descricao || '',
        dentista: {
          nome: pedidoData.dentista?.nome || 'Dentista não informado',
          id: pedidoData.dentista?.id || 0,
          cro: pedidoData.dentista?.cro,
          especialidade: pedidoData.dentista?.especialidade,
          telefone: pedidoData.dentista?.telefone,
          email: pedidoData.dentista?.email
        }
      }
      
      setPedido(pedidoFormatado)
      setNovoStatus(pedidoFormatado.status)
      setNovoValor(pedidoFormatado.valorCobrado > 0 ? pedidoFormatado.valorCobrado.toString() : '')
      setNovoPrazo(pedidoFormatado.dataPrevistaEntrega || '')
      
      // Carrega etapas separadamente
      await carregarEtapas(id)
      
    } catch (error: any) {
      console.error('💥 Erro ao carregar pedido:', error)
      setErro(error.message || 'Erro ao carregar dados do pedido')
    } finally {
      setCarregando(false)
    }
  }

  const carregarEtapas = async (pedidoId: string) => {
    try {
      console.log(`🔄 Buscando etapas do pedido ${pedidoId}...`)
      
      const response = await fetch(`http://localhost:8080/api/etapas/pedido/${pedidoId}`, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      })
      
      if (response.ok) {
        const etapasData = await response.json()
        console.log('✅ Etapas carregadas:', etapasData)
        
        const etapasFormatadas: Etapa[] = etapasData.map((etapa: any) => ({
          id: etapa.id,
          nomeEtapa: etapa.nomeEtapa || etapa.nome || 'Etapa',
          observacoes: etapa.observacoes || etapa.descricao || '',
          status: etapa.status || 'PENDENTE',
          ordem: etapa.ordem || 0,
          dataCriacao: etapa.dataCriacao,
          dataConclusao: etapa.dataConclusao,
          responsavel: etapa.responsavel
        })).sort((a: Etapa, b: Etapa) => a.ordem - b.ordem)
        
        setEtapas(etapasFormatadas)
      } else if (response.status !== 404) {
        console.warn('⚠️ Não foi possível carregar etapas:', response.status)
      }
      
    } catch (error) {
      console.error('💥 Erro ao carregar etapas:', error)
    }
  }

  const handleAprovarPedido = async () => {
    try {
      setSalvando(true)
      
      // Primeiro muda para AGUARDANDO_APROVACAO
      if (pedido?.status !== 'AGUARDANDO_APROVACAO') {
        console.log('🔄 Mudando status para AGUARDANDO_APROVACAO...')
        const statusResponse = await fetch(`http://localhost:8080/api/pedidos/${id}/status?status=AGUARDANDO_APROVACAO`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        })
        
        if (!statusResponse.ok) {
          throw new Error('Erro ao mudar status para AGUARDANDO_APROVACAO')
        }
      }
      
      // Depois aprova
      console.log('✅ Aprovando pedido...')
      const aprovarResponse = await fetch(`http://localhost:8080/api/pedidos/${id}/aprovar`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      })
      
      const data = await aprovarResponse.json()
      console.log('📥 Resposta:', data)
      
      if (!aprovarResponse.ok) {
        throw new Error(data.message || 'Erro ao aprovar pedido')
      }
      
      // Atualiza localmente
      if (pedido) {
        setPedido({
          ...pedido,
          status: 'APROVADO'
        })
      }
      
      setShowAprovarModal(false)
      alert('✅ Pedido aprovado com sucesso!')
      
    } catch (error: any) {
      console.error('❌ Erro:', error)
      alert('❌ Erro ao aprovar pedido: ' + error.message)
    } finally {
      setSalvando(false)
    }
  }

  const handleAtualizarStatus = async () => {
    if (!novoStatus) return
    
    try {
      setSalvando(true)
      
      console.log('🔄 Atualizando status para:', novoStatus)
      
      const response = await fetch(`http://localhost:8080/api/pedidos/${id}/status?status=${encodeURIComponent(novoStatus)}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      })
      
      const data = await response.json()
      console.log('📥 Resposta:', data)
      
      if (!response.ok) {
        throw new Error(data.error || `Erro ${response.status}`)
      }
      
      // Atualiza localmente
      if (pedido) {
        setPedido({
          ...pedido,
          status: novoStatus,
          // Se for status final, atualiza data de entrega
          ...(novoStatus === 'FINALIZADO' || novoStatus === 'ENTREGUE' || novoStatus === 'CONCLUIDO' ? {
            dataEntrega: new Date().toISOString().split('T')[0]
          } : {})
        })
      }
      
      setShowStatusModal(false)
      alert('✅ Status atualizado com sucesso!')
      
    } catch (error: any) {
      console.error('❌ Erro:', error)
      alert('❌ Erro ao atualizar status: ' + error.message)
    } finally {
      setSalvando(false)
    }
  }

  const handleAtualizarValor = async () => {
    const valorNumerico = parseFloat(novoValor)
    if (isNaN(valorNumerico) || valorNumerico <= 0) {
      alert('Por favor, insira um valor válido')
      return
    }
    
    try {
      setSalvando(true)
      
      console.log('💰 Atualizando valor para:', valorNumerico)
      
      const response = await fetch(`http://localhost:8080/api/pedidos/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({
          valorCobrado: valorNumerico
        })
      })
      
      const data = await response.json()
      console.log('📥 Resposta:', data)
      
      if (!response.ok) {
        throw new Error(data.error || `Erro ${response.status}`)
      }
      
      // Atualiza localmente
      if (pedido) {
        setPedido({
          ...pedido,
          valorCobrado: valorNumerico
        })
      }
      
      setShowValorModal(false)
      alert('✅ Valor atualizado com sucesso!')
      
    } catch (error: any) {
      console.error('❌ Erro:', error)
      alert('❌ Erro ao atualizar valor: ' + error.message)
    } finally {
      setSalvando(false)
    }
  }

  const handleAtualizarPrazo = async () => {
    if (!novoPrazo) return
    
    try {
      setSalvando(true)
      
      console.log('📅 Atualizando prazo para:', novoPrazo)
      
      const response = await fetch(`http://localhost:8080/api/pedidos/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({
          dataPrevistaEntrega: novoPrazo
        })
      })
      
      const data = await response.json()
      console.log('📥 Resposta:', data)
      
      if (!response.ok) {
        throw new Error(data.error || `Erro ${response.status}`)
      }
      
      // Atualiza localmente
      if (pedido) {
        setPedido({
          ...pedido,
          dataPrevistaEntrega: novoPrazo
        })
      }
      
      setShowPrazoModal(false)
      alert('✅ Prazo atualizado com sucesso!')
      
    } catch (error: any) {
      console.error('❌ Erro:', error)
      alert('❌ Erro ao atualizar prazo: ' + error.message)
    } finally {
      setSalvando(false)
    }
  }

  const handleAdicionarEtapa = async () => {
    if (!nomeEtapa.trim()) {
      alert('Por favor, insira um nome para a etapa')
      return
    }
    
    try {
      setSalvando(true)
      
      console.log('➕ Adicionando nova etapa...')
      
      const novaEtapa = {
        pedido: { id: parseInt(id) },
        nomeEtapa: nomeEtapa,
        observacoes: observacoes,
        status: 'PENDENTE',
        ordem: etapas.length + 1
      }
      
      const response = await fetch(`http://localhost:8080/api/etapas`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify(novaEtapa)
      })
      
      const data = await response.json()
      console.log('📥 Resposta:', data)
      
      if (!response.ok) {
        throw new Error(data.error || `Erro ${response.status}`)
      }
      
      // Recarrega etapas
      await carregarEtapas(id)
      
      setNomeEtapa('')
      setObservacoes('')
      setShowEtapaModal(false)
      alert('✅ Etapa adicionada com sucesso!')
      
    } catch (error: any) {
      console.error('❌ Erro:', error)
      alert('❌ Erro ao adicionar etapa: ' + error.message)
    } finally {
      setSalvando(false)
    }
  }

  const handleAtualizarEtapa = async (etapaId: number, novoStatus: string) => {
    try {
      console.log(`🔄 Atualizando etapa ${etapaId} para status: ${novoStatus}`)
      
      const response = await fetch(`http://localhost:8080/api/etapas/${etapaId}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({ 
          status: novoStatus
        })
      })
      
      const data = await response.json()
      console.log('📥 Resposta:', data)
      
      if (!response.ok) {
        throw new Error(data.error || `Erro ${response.status}`)
      }
      
      // Atualiza localmente
      setEtapas(etapas.map(etapa => 
        etapa.id === etapaId ? { 
          ...etapa, 
          status: novoStatus,
          dataConclusao: novoStatus === 'CONCLUIDA' ? new Date().toISOString() : etapa.dataConclusao
        } : etapa
      ))
      
      alert('✅ Etapa atualizada!')
      
    } catch (error: any) {
      console.error('❌ Erro:', error)
      alert('❌ Erro ao atualizar etapa: ' + error.message)
    }
  }

  const handleFinalizarPedido = async () => {
    if (!window.confirm('Tem certeza que deseja finalizar este pedido?')) return
    
    try {
      setSalvando(true)
      
      console.log('🏁 Finalizando pedido...')
      
      const response = await fetch(`http://localhost:8080/api/pedidos/${id}/status?status=FINALIZADO`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      })
      
      const data = await response.json()
      console.log('📥 Resposta:', data)
      
      if (!response.ok) {
        throw new Error(data.error || `Erro ${response.status}`)
      }
      
      // Atualiza localmente
      if (pedido) {
        setPedido({
          ...pedido,
          status: 'FINALIZADO',
          dataEntrega: new Date().toISOString().split('T')[0]
        })
      }
      
      alert('✅ Pedido finalizado com sucesso!')
      
    } catch (error: any) {
      console.error('❌ Erro ao finalizar pedido:', error)
      alert('❌ Erro ao finalizar pedido: ' + error.message)
    } finally {
      setSalvando(false)
    }
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

  const formatarTipoServico = (tipo: string) => {
    return tipo
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, l => l.toUpperCase())
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'RASCUNHO': return 'bg-gray-100 text-gray-800'
      case 'AGUARDANDO_APROVACAO': return 'bg-yellow-100 text-yellow-800'
      case 'APROVADO': return 'bg-blue-100 text-blue-800'
      case 'EM_PRODUCAO': return 'bg-purple-100 text-purple-800'
      case 'PRODUCAO': return 'bg-purple-100 text-purple-800'
      case 'FINALIZADO': return 'bg-green-100 text-green-800'
      case 'ENTREGUE': return 'bg-green-100 text-green-800'
      case 'CANCELADO': return 'bg-red-100 text-red-800'
      default: return 'bg-gray-100 text-gray-800'
    }
  }

  const getStatusEtapaColor = (status: string) => {
    switch (status) {
      case 'PENDENTE': return 'bg-gray-100 text-gray-800'
      case 'EM_ANDAMENTO': return 'bg-blue-100 text-blue-800'
      case 'CONCLUIDA': return 'bg-green-100 text-green-800'
      case 'ATRASADA': return 'bg-red-100 text-red-800'
      default: return 'bg-gray-100 text-gray-800'
    }
  }

  if (carregando) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-green-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="mt-4 text-gray-600">Carregando detalhes do pedido...</p>
        </div>
      </div>
    )
  }

  if (erro || !pedido) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="bg-red-50 border border-red-200 rounded-lg p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <svg className="h-5 w-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                </svg>
              </div>
              <div className="ml-3">
                <h3 className="text-lg font-medium text-red-800">Erro ao carregar pedido</h3>
                <p className="text-red-700 mt-2">{erro || 'Pedido não encontrado'}</p>
              </div>
            </div>
            <div className="mt-4">
              <button
                onClick={() => router.push('/protetico/pedidos')}
                className="inline-flex items-center px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                ← Voltar para lista de pedidos
              </button>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => router.push('/protetico/pedidos')}
                className="text-gray-600 hover:text-gray-900"
              >
                ← Voltar
              </button>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">Pedido #{pedido.codigo}</h1>
                <p className="text-gray-600">
                  {formatarTipoServico(pedido.tipoServico)} • {formatarData(pedido.dataEntrada)}
                </p>
              </div>
            </div>
            <div className="flex items-center space-x-3">
              <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(pedido.status)}`}>
                {pedido.status.replace(/_/g, ' ')}
              </span>
            </div>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Grid com duas colunas */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Coluna 1: Informações do Pedido */}
          <div className="lg:col-span-2 space-y-6">
            {/* Card Informações do Pedido */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">📋 Informações do Pedido</h2>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Tipo de Serviço</label>
                  <p className="mt-1 text-sm text-gray-900 font-medium">
                    {formatarTipoServico(pedido.tipoServico)}
                  </p>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700">Valor</label>
                  <p className="mt-1 text-sm text-gray-900 font-medium">
                    {formatarValor(pedido.valorCobrado)}
                  </p>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700">Data de Entrada</label>
                  <p className="mt-1 text-sm text-gray-900">{formatarData(pedido.dataEntrada)}</p>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700">Previsão de Entrega</label>
                  <p className="mt-1 text-sm text-gray-900">
                    {pedido.dataPrevistaEntrega ? formatarData(pedido.dataPrevistaEntrega) : 'Não definida'}
                  </p>
                </div>
              </div>
              
              {pedido.dataEntrega && (
                <div className="mt-4 pt-4 border-t border-gray-200">
                  <label className="block text-sm font-medium text-gray-700">Data de Entrega</label>
                  <p className="mt-1 text-sm text-gray-900">{formatarData(pedido.dataEntrega)}</p>
                </div>
              )}
              
              <div className="mt-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">Informações Detalhadas</label>
                <div className="bg-gray-50 rounded-lg p-4">
                  <p className="text-sm text-gray-700 whitespace-pre-line">
                    {pedido.informacoesDetalhadas || 'Nenhuma informação adicional fornecida.'}
                  </p>
                </div>
              </div>
            </div>

            {/* Card Etapas do Pedido */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-lg font-semibold text-gray-900">🔄 Etapas do Processo</h2>
                <button
                  onClick={() => setShowEtapaModal(true)}
                  className="text-sm bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700"
                >
                  + Nova Etapa
                </button>
              </div>
              
              {etapas.length === 0 ? (
                <div className="text-center py-8">
                  <p className="text-gray-500">Nenhuma etapa cadastrada para este pedido.</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {etapas.map((etapa) => (
                    <div key={etapa.id} className="border border-gray-200 rounded-lg p-4">
                      <div className="flex justify-between items-start mb-2">
                        <div>
                          <h3 className="font-medium text-gray-900">{etapa.nomeEtapa}</h3>
                          <p className="text-sm text-gray-600 mt-1">{etapa.observacoes}</p>
                        </div>
                        <span className={`px-2 py-1 text-xs rounded-full ${getStatusEtapaColor(etapa.status)}`}>
                          {etapa.status.replace(/_/g, ' ')}
                        </span>
                      </div>
                      
                      <div className="flex justify-between items-center mt-4">
                        <div className="text-xs text-gray-500">
                          {etapa.dataCriacao && `Criação: ${formatarData(etapa.dataCriacao)}`}
                          {etapa.dataConclusao && ` • Conclusão: ${formatarData(etapa.dataConclusao)}`}
                        </div>
                        
                        <div className="flex space-x-2">
                          {etapa.status !== 'CONCLUIDA' && (
                            <button
                              onClick={() => handleAtualizarEtapa(etapa.id, 'CONCLUIDA')}
                              className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded hover:bg-green-200"
                            >
                              Concluir
                            </button>
                          )}
                          {etapa.status !== 'EM_ANDAMENTO' && etapa.status !== 'CONCLUIDA' && (
                            <button
                              onClick={() => handleAtualizarEtapa(etapa.id, 'EM_ANDAMENTO')}
                              className="text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded hover:bg-blue-200"
                            >
                              Iniciar
                            </button>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Coluna 2: Ações e Informações */}
          <div className="space-y-6">
            {/* Card Ações Rápidas */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">⚡ Ações Rápidas</h2>
              
              <div className="space-y-3">
                {pedido.status === 'AGUARDANDO_APROVACAO' && (
                  <button
                    onClick={() => setShowAprovarModal(true)}
                    className="w-full bg-green-600 text-white py-2 px-4 rounded-lg hover:bg-green-700 transition-colors"
                  >
                    ✅ Aprovar Pedido
                  </button>
                )}
                
                <button
                  onClick={() => setShowStatusModal(true)}
                  className="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors"
                >
                  🔄 Alterar Status
                </button>
                
                <button
                  onClick={() => setShowValorModal(true)}
                  className="w-full bg-purple-600 text-white py-2 px-4 rounded-lg hover:bg-purple-700 transition-colors"
                >
                  💰 Atualizar Valor
                </button>
                
                <button
                  onClick={() => setShowPrazoModal(true)}
                  className="w-full bg-yellow-600 text-white py-2 px-4 rounded-lg hover:bg-yellow-700 transition-colors"
                >
                  📅 Alterar Prazo
                </button>
                
                {pedido.status !== 'FINALIZADO' && pedido.status !== 'ENTREGUE' && (
                  <button
                    onClick={handleFinalizarPedido}
                    className="w-full bg-green-700 text-white py-2 px-4 rounded-lg hover:bg-green-800 transition-colors"
                  >
                    🏁 Finalizar Pedido
                  </button>
                )}
              </div>
            </div>

            {/* Card Informações do Dentista */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">👨‍⚕️ Dentista</h2>
              
              <div className="space-y-3">
                <div>
                  <p className="font-medium text-gray-900">{pedido.dentista.nome}</p>
                  {pedido.dentista.especialidade && (
                    <p className="text-sm text-gray-600">{pedido.dentista.especialidade}</p>
                  )}
                </div>
                
                {pedido.dentista.cro && (
                  <div>
                    <p className="text-sm text-gray-700">CRO: {pedido.dentista.cro}</p>
                  </div>
                )}
                
                {pedido.dentista.email && (
                  <div>
                    <p className="text-sm text-gray-700">Email: {pedido.dentista.email}</p>
                  </div>
                )}
                
                {pedido.dentista.telefone && (
                  <div>
                    <p className="text-sm text-gray-700">Telefone: {pedido.dentista.telefone}</p>
                  </div>
                )}
              </div>
              
              <div className="mt-4 pt-4 border-t border-gray-200">
                <button
                  onClick={() => {
                    if (!pedido.dentista.telefone) {
                      alert('Telefone do dentista não disponível')
                      return
                    }
                    const mensagem = `Olá Dr. ${pedido.dentista.nome}, tudo bem? Gostaria de conversar sobre o pedido #${pedido.codigo}.`
                    window.open(`https://wa.me/55${pedido.dentista.telefone}?text=${encodeURIComponent(mensagem)}`, '_blank')
                  }}
                  className="w-full bg-green-100 text-green-700 py-2 px-4 rounded-lg hover:bg-green-200 transition-colors"
                >
                  💬 Entrar em Contato
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Modal de Aprovação */}
      {showAprovarModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">✅ Aprovar Pedido</h3>
            <p className="text-gray-600 mb-6">
              Tem certeza que deseja aprovar o pedido #{pedido.codigo}?
              Após aprovação, o pedido será movido para produção.
            </p>
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowAprovarModal(false)}
                className="px-4 py-2 text-gray-700 hover:text-gray-900"
                disabled={salvando}
              >
                Cancelar
              </button>
              <button
                onClick={handleAprovarPedido}
                className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                disabled={salvando}
              >
                {salvando ? 'Aprovando...' : 'Sim, Aprovar'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de Status */}
      {showStatusModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">🔄 Alterar Status</h3>
            
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Novo Status
              </label>
              <select
                value={novoStatus}
                onChange={(e) => setNovoStatus(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              >
                <option value="RASCUNHO">Rascunho</option>
                <option value="AGUARDANDO_APROVACAO">Aguardando Aprovação</option>
                <option value="APROVADO">Aprovado</option>
                <option value="EM_PRODUCAO">Em Produção</option>
                <option value="FINALIZADO">Finalizado</option>
                <option value="ENTREGUE">Entregue</option>
                <option value="CANCELADO">Cancelado</option>
              </select>
            </div>
            
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowStatusModal(false)}
                className="px-4 py-2 text-gray-700 hover:text-gray-900"
                disabled={salvando}
              >
                Cancelar
              </button>
              <button
                onClick={handleAtualizarStatus}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                disabled={salvando}
              >
                {salvando ? 'Atualizando...' : 'Atualizar Status'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de Valor */}
      {showValorModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">💰 Atualizar Valor</h3>
            
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Novo Valor (R$)
              </label>
              <input
                type="number"
                step="0.01"
                min="0"
                value={novoValor}
                onChange={(e) => setNovoValor(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
                placeholder="Ex: 350.00"
              />
            </div>
            
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowValorModal(false)}
                className="px-4 py-2 text-gray-700 hover:text-gray-900"
                disabled={salvando}
              >
                Cancelar
              </button>
              <button
                onClick={handleAtualizarValor}
                className="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
                disabled={salvando}
              >
                {salvando ? 'Atualizando...' : 'Atualizar Valor'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de Prazo */}
      {showPrazoModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">📅 Alterar Prazo de Entrega</h3>
            
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nova Data de Previsão
              </label>
              <input
                type="date"
                value={novoPrazo}
                onChange={(e) => setNovoPrazo(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
            </div>
            
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowPrazoModal(false)}
                className="px-4 py-2 text-gray-700 hover:text-gray-900"
                disabled={salvando}
              >
                Cancelar
              </button>
              <button
                onClick={handleAtualizarPrazo}
                className="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700"
                disabled={salvando}
              >
                {salvando ? 'Atualizando...' : 'Atualizar Prazo'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de Nova Etapa */}
      {showEtapaModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">➕ Nova Etapa</h3>
            
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nome da Etapa *
              </label>
              <input
                type="text"
                value={nomeEtapa}
                onChange={(e) => setNomeEtapa(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
                placeholder="Ex: Modelagem 3D"
              />
            </div>
            
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Observações
              </label>
              <textarea
                value={observacoes}
                onChange={(e) => setObservacoes(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 h-32"
                placeholder="Descreva o que será feito nesta etapa..."
              />
            </div>
            
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowEtapaModal(false)}
                className="px-4 py-2 text-gray-700 hover:text-gray-900"
                disabled={salvando}
              >
                Cancelar
              </button>
              <button
                onClick={handleAdicionarEtapa}
                className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                disabled={salvando}
              >
                {salvando ? 'Adicionando...' : 'Adicionar Etapa'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}