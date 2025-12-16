// app/protetico/dashboard/perfil/page.tsx
"use client"

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'

// Tipos
interface Protetico {
  id: number
  nome: string
  email: string
  tipo: string
  registroProfissional: string
  especializacao: string
  valorHora: number | null
  capacidadePedidosSimultaneos: number
  dataCriacao: string
  ativo: boolean
  telefone?: string
  endereco?: string
  cidade?: string
  estado?: string
  cep?: string
  avatarUrl?: string
}

interface ServicoProtetico {
  id: number
  tipoServico: string
  nomeServico: string
  descricao: string
  preco: number
  tempoMedioDias: number
  ativo: boolean
  proteticoId: number
  
  // NOVOS CAMPOS para terceirização
  politicaExecucao: 'proprio' | 'terceirizado' | 'proprio_ou_terceirizado' | 'nao_oferecido'
  precoTerceirizado?: number
  prazoTerceirizadoDias?: number
  terceirizadoPreferidoId?: number
  terceirizadoPreferidoNome?: string
  observacoesTerceirizacao?: string
}

interface TipoServicoDisponivel {
  codigo: string
  nome: string
  descricao: string
  categoria: string
}

interface Estatisticas {
  totalServicos: number
  servicosAtivos: number
  podeTerceirizar: number
  executaProprio: number
  precoMedio: number
  servicosMaisCaros: ServicoProtetico[]
}

export default function PerfilPage() {
  const router = useRouter()
  const [protetico, setProtetico] = useState<Protetico | null>(null)
  const [servicos, setServicos] = useState<ServicoProtetico[]>([])
  const [tiposServicoDisponiveis, setTiposServicoDisponiveis] = useState<TipoServicoDisponivel[]>([])
  const [estatisticas, setEstatisticas] = useState<Estatisticas | null>(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [mostrarModal, setMostrarModal] = useState(false)
  const [novoServico, setNovoServico] = useState({
    tipoServico: '',
    preco: 0,
    tempoMedioDias: 7,
    // NOVOS CAMPOS para terceirização
    politicaExecucao: 'proprio' as 'proprio' | 'terceirizado' | 'proprio_ou_terceirizado' | 'nao_oferecido',
    precoTerceirizado: 0,
    prazoTerceirizadoDias: 0,
    terceirizadoPreferidoId: undefined as number | undefined,
    observacoesTerceirizacao: ''
  })
  const [editandoServico, setEditandoServico] = useState<ServicoProtetico | null>(null)

  useEffect(() => {
    carregarDados()
  }, [])

  const carregarDados = async () => {
    try {
      setCarregando(true)
      setErro('')
      
      // 1. Pega usuário do localStorage
      const usuarioJSON = localStorage.getItem('usuario')
      if (!usuarioJSON) {
        throw new Error('Usuário não encontrado. Faça login novamente.')
      }
      
      const usuario = JSON.parse(usuarioJSON)
      
      // 2. Carrega dados do protético da API
      const proteticoRes = await fetch(`http://localhost:8080/api/proteticos/${usuario.id}`)
      
      if (!proteticoRes.ok) {
        throw new Error(`Erro ${proteticoRes.status} ao buscar protético`)
      }
      
      const proteticoData: Protetico = await proteticoRes.json()
      setProtetico(proteticoData)
      
      // 3. Carrega serviços do protético
      await carregarServicos(proteticoData.id)
      
      // 4. Carrega tipos de serviço disponíveis
      await carregarTiposServico()
      
    } catch (error: any) {
      console.error('💥 Erro:', error)
      setErro(error.message || 'Erro ao carregar dados')
    } finally {
      setCarregando(false)
    }
  }

  const carregarServicos = async (proteticoId: number) => {
    try {
      const servicosRes = await fetch(`http://localhost:8080/api/servicos-protetico/protetico/${proteticoId}`)
      
      if (servicosRes.ok) {
        const servicosData = await servicosRes.json()
        
        // Mapeia os dados recebidos para a interface do frontend
        const servicosMapeados = servicosData.map((item: any) => ({
          id: item.id,
          tipoServico: item.tipoServico,
          nomeServico: item.nomeServico || item.tipoServico.replace(/_/g, ' '),
          descricao: item.descricao || '',
          preco: item.preco,
          tempoMedioDias: item.tempoMedioDias || 0,
          ativo: item.ativo,
          proteticoId: item.proteticoId,
          // NOVOS CAMPOS
          politicaExecucao: item.politicaExecucao || 'proprio',
          precoTerceirizado: item.precoTerceirizado,
          prazoTerceirizadoDias: item.prazoTerceirizadoDias,
          terceirizadoPreferidoId: item.terceirizadoPreferidoId,
          terceirizadoPreferidoNome: item.terceirizadoPreferidoNome,
          observacoesTerceirizacao: item.observacoesTerceirizacao
        }))
        
        setServicos(Array.isArray(servicosMapeados) ? servicosMapeados : [])
        
        // Calcula estatísticas
        calcularEstatisticas(servicosMapeados)
      }
    } catch (error) {
      console.error('❌ Erro ao carregar serviços:', error)
    }
  }

  const carregarTiposServico = async () => {
    try {
      // Fallback com tipos padrão (você pode implementar endpoint depois)
      setTiposServicoDisponiveis([
        { codigo: 'COROA', nome: 'Coroa', descricao: 'Coroa dentária', categoria: 'RESTAURACAO' },
        { codigo: 'PONTE_FIXA', nome: 'Ponte Fixa', descricao: 'Ponte fixa', categoria: 'RESTAURACAO' },
        { codigo: 'PROTESE_TOTAL', nome: 'Prótese Total', descricao: 'Prótese dentária completa', categoria: 'PROTESE' },
        { codigo: 'PROTESE_PARCIAL', nome: 'Prótese Parcial', descricao: 'Prótese dentária parcial', categoria: 'PROTESE' },
        { codigo: 'ZIRCONIA', nome: 'Zircônia', descricao: 'Coroa em zircônia', categoria: 'ESTETICA' },
        { codigo: 'RESINA', nome: 'Resina', descricao: 'Restauração em resina', categoria: 'RESTAURACAO' },
        { codigo: 'IMPLANTE', nome: 'Implante', descricao: 'Implante dentário', categoria: 'IMPLANTE' },
        { codigo: 'ORTODONTIA', nome: 'Ortodontia', descricao: 'Aparelho ortodôntico', categoria: 'ORTODONTIA' },
        { codigo: 'PROVISORIO', nome: 'Provisório', descricao: 'Prótese provisória', categoria: 'PROTESE' },
        { codigo: 'OUTRO', nome: 'Outro', descricao: 'Outro tipo de serviço', categoria: 'GERAL' }
      ])
    } catch (error) {
      console.error('❌ Erro ao carregar tipos de serviço:', error)
    }
  }

  const calcularEstatisticas = (servicosData: ServicoProtetico[]) => {
    const servicosAtivos = servicosData.filter(s => s.ativo)
    const podeTerceirizar = servicosAtivos.filter(s => 
      s.politicaExecucao === 'terceirizado' || s.politicaExecucao === 'proprio_ou_terceirizado'
    ).length
    const executaProprio = servicosAtivos.filter(s => 
      s.politicaExecucao === 'proprio' || s.politicaExecucao === 'proprio_ou_terceirizado'
    ).length
    
    const precoMedio = servicosAtivos.length > 0 
      ? servicosAtivos.reduce((sum, s) => sum + s.preco, 0) / servicosAtivos.length 
      : 0
    
    const servicosMaisCaros = [...servicosAtivos]
      .sort((a, b) => b.preco - a.preco)
      .slice(0, 3)
    
    setEstatisticas({
      totalServicos: servicosData.length,
      servicosAtivos: servicosAtivos.length,
      podeTerceirizar,
      executaProprio,
      precoMedio,
      servicosMaisCaros
    })
  }

  const adicionarServico = async () => {
    if (!protetico || !novoServico.tipoServico) {
      alert('Selecione o tipo de serviço')
      return
    }

    // Validação baseada na política
    if (novoServico.politicaExecucao === 'proprio' && novoServico.preco <= 0) {
      alert('Preço é obrigatório para serviços próprios')
      return
    }

    if ((novoServico.politicaExecucao === 'terceirizado' || novoServico.politicaExecucao === 'proprio_ou_terceirizado') && 
        novoServico.precoTerceirizado <= 0) {
      alert('Preço terceirizado é obrigatório')
      return
    }

    try {
      const payload = {
        tipoServico: novoServico.tipoServico,
        preco: novoServico.preco,
        tempoMedioDias: novoServico.tempoMedioDias,
        descricao: '',
        ativo: true,
        // NOVOS CAMPOS
        politicaExecucao: novoServico.politicaExecucao,
        precoTerceirizado: novoServico.precoTerceirizado || undefined,
        prazoTerceirizadoDias: novoServico.prazoTerceirizadoDias || undefined,
        terceirizadoPreferidoId: novoServico.terceirizadoPreferidoId || undefined,
        observacoesTerceirizacao: novoServico.observacoesTerceirizacao || undefined
      }
      
      const response = await fetch(`http://localhost:8080/api/servicos-protetico/protetico/${protetico.id}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Erro ${response.status}: ${errorText}`)
      }

      await carregarServicos(protetico.id)
      
      // Limpa o formulário
      setNovoServico({
        tipoServico: '',
        preco: 0,
        tempoMedioDias: 7,
        politicaExecucao: 'proprio',
        precoTerceirizado: 0,
        prazoTerceirizadoDias: 0,
        terceirizadoPreferidoId: undefined,
        observacoesTerceirizacao: ''
      })
      setMostrarModal(false)
      
      alert('Serviço adicionado com sucesso!')
      
    } catch (error: any) {
      console.error('❌ Erro ao adicionar serviço:', error)
      alert(`Erro: ${error.message}`)
    }
  }

  const atualizarServico = async (servico: ServicoProtetico) => {
    try {
      const payload = {
        preco: servico.preco,
        descricao: servico.descricao || '',
        tempoMedioDias: servico.tempoMedioDias,
        ativo: servico.ativo,
        // NOVOS CAMPOS
        politicaExecucao: servico.politicaExecucao,
        precoTerceirizado: servico.precoTerceirizado || undefined,
        prazoTerceirizadoDias: servico.prazoTerceirizadoDias || undefined,
        terceirizadoPreferidoId: servico.terceirizadoPreferidoId || undefined,
        observacoesTerceirizacao: servico.observacoesTerceirizacao || undefined
      }
      
      const response = await fetch(`http://localhost:8080/api/servicos-protetico/protetico/${protetico?.id}/tipo/${servico.tipoServico}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Erro ${response.status}: ${errorText}`)
      }

      await carregarServicos(protetico!.id)
      
    } catch (error: any) {
      console.error('❌ Erro ao atualizar serviço:', error)
      alert(`Erro: ${error.message}`)
    }
  }

  const toggleServicoStatus = async (servico: ServicoProtetico) => {
    try {
      const novoStatus = !servico.ativo
      
      const response = await fetch(`http://localhost:8080/api/servicos-protetico/protetico/${protetico?.id}/tipo/${servico.tipoServico}/status`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({ ativo: novoStatus })
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Erro ${response.status}: ${errorText}`)
      }

      await carregarServicos(protetico!.id)
      
    } catch (error: any) {
      console.error('❌ Erro ao alterar status:', error)
      alert(`Erro: ${error.message}`)
    }
  }

  const removerServico = async (tipoServico: string) => {
    if (!confirm(`Tem certeza que deseja remover o serviço ${tipoServico}?`)) {
      return
    }

    try {
      const response = await fetch(`http://localhost:8080/api/servicos-protetico/protetico/${protetico?.id}/tipo/${tipoServico}`, {
        method: 'DELETE'
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Erro ${response.status}: ${errorText}`)
      }

      await carregarServicos(protetico!.id)
      
    } catch (error: any) {
      console.error('❌ Erro ao remover serviço:', error)
      alert(`Erro: ${error.message}`)
    }
  }

  const formatarValor = (valor: number) => {
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

  const getPoliticaInfo = (politica: string) => {
    switch(politica) {
      case 'proprio': return { icon: '🏭', color: 'bg-blue-100 text-blue-800', label: 'Executo' }
      case 'terceirizado': return { icon: '🤝', color: 'bg-purple-100 text-purple-800', label: 'Terceirizo' }
      case 'proprio_ou_terceirizado': return { icon: '🔄', color: 'bg-green-100 text-green-800', label: 'Flexível' }
      case 'nao_oferecido': return { icon: '❌', color: 'bg-gray-100 text-gray-800', label: 'Não ofereço' }
      default: return { icon: '❓', color: 'bg-gray-100 text-gray-800', label: 'Indefinido' }
    }
  }

  const getTiposServicoNaoAdicionados = () => {
    const tiposAdicionados = servicos.map(s => s.tipoServico)
    return tiposServicoDisponiveis.filter(tipo => 
      !tiposAdicionados.includes(tipo.codigo)
    )
  }

  if (carregando) {
    return (
      <div className="p-4 md:p-8 space-y-8">
        <div className="animate-pulse space-y-6">
          <div className="h-8 bg-gray-200 rounded w-1/3"></div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="h-32 bg-gray-200 rounded"></div>
            <div className="h-32 bg-gray-200 rounded"></div>
          </div>
          <div className="h-64 bg-gray-200 rounded"></div>
        </div>
      </div>
    )
  }

  if (erro) {
    return (
      <div className="p-4 md:p-8 space-y-8">
        <h1 className="text-2xl font-bold text-gray-800">Erro</h1>
        <div className="bg-red-50 border border-red-200 rounded-xl p-6">
          <p className="text-red-700 mb-4">{erro}</p>
          <button
            onClick={carregarDados}
            className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
          >
            Tentar Novamente
          </button>
        </div>
      </div>
    )
  }

  if (!protetico || !estatisticas) return null

  return (
    <div className="p-4 md:p-8 space-y-8">
      {/* Cabeçalho */}
      <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-6 border border-blue-100">
        <div className="flex flex-col md:flex-row md:items-center justify-between">
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-gray-800">
              Olá, <span className="text-blue-600">{protetico.nome}</span>!
            </h1>
            <p className="text-gray-600 mt-2">
              Registro Profissional: {protetico.registroProfissional} • Especialização: {protetico.especializacao}
            </p>
          </div>
          <div className="flex flex-wrap gap-3 mt-4 md:mt-0">
            <Link
              href="/protetico/dashboard/perfil/editar"
              className="px-4 py-2 bg-white text-blue-600 border border-blue-200 rounded-lg hover:bg-blue-50 font-medium"
            >
              ✏️ Editar Perfil
            </Link>
            <button
              onClick={() => setMostrarModal(true)}
              className="px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-lg hover:from-green-600 hover:to-emerald-700 font-medium"
            >
              ➕ Adicionar Serviço
            </button>
          </div>
        </div>
      </div>

      {/* Dashboard Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Card 1: Serviços Totais */}
        <div className="bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-200 rounded-2xl p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-blue-700">Serviços Totais</p>
              <p className="text-3xl font-bold text-blue-800 mt-2">{estatisticas.totalServicos}</p>
              <p className="text-sm text-blue-600 mt-1">{estatisticas.servicosAtivos} ativos</p>
            </div>
            <div className="text-blue-400">
              <svg className="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
          </div>
        </div>

        {/* Card 2: Preço Médio */}
        <div className="bg-gradient-to-br from-green-50 to-emerald-100 border border-green-200 rounded-2xl p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-green-700">Preço Médio</p>
              <p className="text-3xl font-bold text-green-800 mt-2">{formatarValor(estatisticas.precoMedio)}</p>
              <p className="text-sm text-green-600 mt-1">por serviço</p>
            </div>
            <div className="text-green-400">
              <svg className="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>

        {/* Card 3: Pode Terceirizar */}
        <div className="bg-gradient-to-br from-purple-50 to-pink-100 border border-purple-200 rounded-2xl p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-purple-700">Pode Terceirizar</p>
              <p className="text-3xl font-bold text-purple-800 mt-2">{estatisticas.podeTerceirizar}</p>
              <p className="text-sm text-purple-600 mt-1">serviços</p>
            </div>
            <div className="text-purple-400">
              <span className="text-3xl">🤝</span>
            </div>
          </div>
        </div>

        {/* Card 4: Executa Próprio */}
        <div className="bg-gradient-to-br from-orange-50 to-amber-100 border border-orange-200 rounded-2xl p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-orange-700">Executa Próprio</p>
              <p className="text-3xl font-bold text-orange-800 mt-2">{estatisticas.executaProprio}</p>
              <p className="text-sm text-orange-600 mt-1">serviços</p>
            </div>
            <div className="text-orange-400">
              <span className="text-3xl">🏭</span>
            </div>
          </div>
        </div>
      </div>

      {/* Seção de Ações Rápidas */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Link
          href="/protetico/dashboard/servicos"
          className="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-lg transition-shadow"
        >
          <div className="flex items-center gap-4">
            <div className="p-3 bg-blue-100 text-blue-700 rounded-xl">
              <span className="text-2xl">📋</span>
            </div>
            <div>
              <h3 className="font-bold text-gray-800">Ver Todos Serviços</h3>
              <p className="text-sm text-gray-600 mt-1">Gerencie e visualize todos seus serviços</p>
            </div>
          </div>
        </Link>

        <Link
          href="/protetico/dashboard/perfil/terceirizacao"
          className="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-lg transition-shadow"
        >
          <div className="flex items-center gap-4">
            <div className="p-3 bg-purple-100 text-purple-700 rounded-xl">
              <span className="text-2xl">🤝</span>
            </div>
            <div>
              <h3 className="font-bold text-gray-800">Configurar Terceirização</h3>
              <p className="text-sm text-gray-600 mt-1">Gerencie serviços que pode repassar</p>
            </div>
          </div>
        </Link>

        <Link
          href="/protetico/dashboard/terceirizacao"
          className="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-lg transition-shadow"
        >
          <div className="flex items-center gap-4">
            <div className="p-3 bg-green-100 text-green-700 rounded-xl">
              <span className="text-2xl">🔄</span>
            </div>
            <div>
              <h3 className="font-bold text-gray-800">Pedidos para Terceirizar</h3>
              <p className="text-sm text-gray-600 mt-1">Veja pedidos disponíveis para repassar</p>
            </div>
          </div>
        </Link>
      </div>

      {/* Seção de Serviços */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-200 overflow-hidden">
        <div className="p-6 border-b border-gray-200">
          <div className="flex flex-col md:flex-row md:items-center justify-between">
            <div>
              <h2 className="text-xl font-bold text-gray-800">Meus Serviços</h2>
              <p className="text-gray-600 mt-1">
                Gerencie os serviços que você oferece e suas políticas de execução
              </p>
            </div>
            <div className="flex items-center gap-3 mt-4 md:mt-0">
              <button
                onClick={carregarDados}
                className="px-4 py-2 text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg font-medium"
              >
                🔄 Atualizar
              </button>
            </div>
          </div>
        </div>

        {/* Lista de Serviços */}
        <div className="p-6">
          {servicos.length === 0 ? (
            <div className="text-center py-12">
              <div className="text-6xl mb-4">📋</div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Nenhum serviço cadastrado</h3>
              <p className="text-gray-600 mb-6">Adicione os serviços que você oferece para começar a receber pedidos</p>
              <button
                onClick={() => setMostrarModal(true)}
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                Adicionar Primeiro Serviço
              </button>
            </div>
          ) : (
            <div className="space-y-4">
              {servicos.map((servico) => {
                const tipoInfo = tiposServicoDisponiveis.find(t => t.codigo === servico.tipoServico)
                const politica = getPoliticaInfo(servico.politicaExecucao)
                
                return (
                  <div 
                    key={servico.id} 
                    className="bg-gray-50 border border-gray-200 rounded-xl p-5 hover:bg-gray-100 transition-colors"
                  >
                    <div className="flex flex-col lg:flex-row lg:items-start justify-between gap-4">
                      <div className="flex-1">
                        <div className="flex items-start gap-4">
                          <div className={`p-3 rounded-xl ${politica.color} flex-shrink-0`}>
                            <span className="text-xl">{politica.icon}</span>
                          </div>
                          <div className="flex-1">
                            <div className="flex flex-wrap items-center gap-3 mb-3">
                              <h4 className="font-bold text-gray-800 text-lg">
                                {tipoInfo?.nome || formatarTipoServico(servico.tipoServico)}
                              </h4>
                              <span className={`inline-flex px-3 py-1 text-xs font-semibold rounded-full ${politica.color}`}>
                                {politica.label}
                              </span>
                              <span className={`inline-flex px-3 py-1 text-xs font-semibold rounded-full ${
                                servico.ativo ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                              }`}>
                                {servico.ativo ? 'Ativo' : 'Inativo'}
                              </span>
                            </div>
                            
                            <p className="text-gray-600 mb-4">
                              {tipoInfo?.descricao || 'Serviço dental'}
                            </p>
                            
                            {/* Informações de preço e prazo */}
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                              {/* Quando executo próprio */}
                              {(servico.politicaExecucao === 'proprio' || servico.politicaExecucao === 'proprio_ou_terceirizado') && (
                                <div className="bg-blue-50 p-3 rounded-lg">
                                  <p className="text-sm font-medium text-blue-700 mb-1">🏭 Quando executo:</p>
                                  <p className="font-bold text-blue-800 text-lg">{formatarValor(servico.preco)}</p>
                                  <p className="text-sm text-blue-600">{servico.tempoMedioDias} dias</p>
                                </div>
                              )}
                              
                              {/* Quando terceirizo */}
                              {(servico.politicaExecucao === 'terceirizado' || servico.politicaExecucao === 'proprio_ou_terceirizado') && (
                                <div className="bg-purple-50 p-3 rounded-lg">
                                  <p className="text-sm font-medium text-purple-700 mb-1">🤝 Quando terceirizo:</p>
                                  <p className="font-bold text-purple-800 text-lg">
                                    {servico.precoTerceirizado ? formatarValor(servico.precoTerceirizado) : 'Não definido'}
                                  </p>
                                  <p className="text-sm text-purple-600">
                                    {servico.prazoTerceirizadoDias ? `${servico.prazoTerceirizadoDias} dias` : 'Prazo não definido'}
                                  </p>
                                </div>
                              )}
                            </div>
                            
                            {/* Observações e terceirizado preferido */}
                            {servico.observacoesTerceirizacao && (
                              <div className="bg-yellow-50 p-3 rounded-lg mb-3">
                                <p className="text-sm font-medium text-yellow-700 mb-1">📝 Observações:</p>
                                <p className="text-sm text-yellow-800">{servico.observacoesTerceirizacao}</p>
                              </div>
                            )}
                            
                            {servico.terceirizadoPreferidoNome && (
                              <div className="text-sm text-gray-600">
                                <span className="font-medium">👤 Terceirizado Preferido:</span> {servico.terceirizadoPreferidoNome}
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                      
                      <div className="flex flex-wrap gap-2">
                        <button
                          onClick={() => setEditandoServico(servico)}
                          className="px-4 py-2 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 font-medium"
                        >
                          ✏️ Editar
                        </button>
                        <button
                          onClick={() => toggleServicoStatus(servico)}
                          className={`px-4 py-2 rounded-lg font-medium ${
                            servico.ativo
                              ? 'bg-yellow-100 text-yellow-700 hover:bg-yellow-200'
                              : 'bg-green-100 text-green-700 hover:bg-green-200'
                          }`}
                        >
                          {servico.ativo ? '⏸️ Desativar' : '▶️ Ativar'}
                        </button>
                        <button
                          onClick={() => removerServico(servico.tipoServico)}
                          className="px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 font-medium"
                        >
                          🗑️ Remover
                        </button>
                      </div>
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>
      </div>

      {/* Modal para Adicionar Serviço */}
      {mostrarModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold text-gray-800">Adicionar Serviço</h3>
              <button
                onClick={() => setMostrarModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                ✕
              </button>
            </div>
            
            <div className="space-y-6">
              {/* Tipo de Serviço */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tipo de Serviço *
                </label>
                <select
                  value={novoServico.tipoServico}
                  onChange={(e) => setNovoServico({...novoServico, tipoServico: e.target.value})}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="">Selecione um serviço</option>
                  {getTiposServicoNaoAdicionados().map((tipo) => (
                    <option key={tipo.codigo} value={tipo.codigo}>
                      {tipo.nome} - {tipo.descricao}
                    </option>
                  ))}
                </select>
              </div>

              {/* Política de Execução */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-3">
                  Como você trabalha com este serviço? *
                </label>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                  {/* Opção 1: Executo na minha oficina */}
                  <label className={`cursor-pointer border-2 rounded-xl p-4 flex items-center gap-3 ${novoServico.politicaExecucao === 'proprio' ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300'}`}>
                    <input
                      type="radio"
                      name="politicaExecucao"
                      value="proprio"
                      checked={novoServico.politicaExecucao === 'proprio'}
                      onChange={(e) => setNovoServico({...novoServico, politicaExecucao: e.target.value as any})}
                      className="text-blue-600"
                    />
                    <div>
                      <div className="font-medium">🏭 Executo na minha oficina</div>
                      <p className="text-sm text-gray-600 mt-1">Produzo eu mesmo em meu laboratório</p>
                    </div>
                  </label>

                  {/* Opção 2: Apenas terceirizado */}
                  <label className={`cursor-pointer border-2 rounded-xl p-4 flex items-center gap-3 ${novoServico.politicaExecucao === 'terceirizado' ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300'}`}>
                    <input
                      type="radio"
                      name="politicaExecucao"
                      value="terceirizado"
                      checked={novoServico.politicaExecucao === 'terceirizado'}
                      onChange={(e) => setNovoServico({...novoServico, politicaExecucao: e.target.value as any})}
                      className="text-blue-600"
                    />
                    <div>
                      <div className="font-medium">🤝 Apenas terceirizado</div>
                      <p className="text-sm text-gray-600 mt-1">Sempre repasso para outro protético</p>
                    </div>
                  </label>

                  {/* Opção 3: Posso executar ou terceirizar */}
                  <label className={`cursor-pointer border-2 rounded-xl p-4 flex items-center gap-3 ${novoServico.politicaExecucao === 'proprio_ou_terceirizado' ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300'}`}>
                    <input
                      type="radio"
                      name="politicaExecucao"
                      value="proprio_ou_terceirizado"
                      checked={novoServico.politicaExecucao === 'proprio_ou_terceirizado'}
                      onChange={(e) => setNovoServico({...novoServico, politicaExecucao: e.target.value as any})}
                      className="text-blue-600"
                    />
                    <div>
                      <div className="font-medium">🔄 Posso executar ou terceirizar</div>
                      <p className="text-sm text-gray-600 mt-1">Depende da disponibilidade/capacidade</p>
                    </div>
                  </label>

                  {/* Opção 4: Não ofereço */}
                  <label className={`cursor-pointer border-2 rounded-xl p-4 flex items-center gap-3 ${novoServico.politicaExecucao === 'nao_oferecido' ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300'}`}>
                    <input
                      type="radio"
                      name="politicaExecucao"
                      value="nao_oferecido"
                      checked={novoServico.politicaExecucao === 'nao_oferecido'}
                      onChange={(e) => setNovoServico({...novoServico, politicaExecucao: e.target.value as any})}
                      className="text-blue-600"
                    />
                    <div>
                      <div className="font-medium">❌ Não trabalho com este serviço</div>
                      <p className="text-sm text-gray-600 mt-1">Não aceito pedidos deste tipo</p>
                    </div>
                  </label>
                </div>
              </div>

              {/* Seção: Preços */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Preço quando executo */}
                {(novoServico.politicaExecucao === 'proprio' || novoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Preço quando executo (R$) *
                    </label>
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={novoServico.preco}
                      onChange={(e) => setNovoServico({...novoServico, preco: parseFloat(e.target.value) || 0})}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                      placeholder="Ex: 500.00"
                      required={novoServico.politicaExecucao === 'proprio'}
                    />
                  </div>
                )}

                {/* Preço quando terceirizo */}
                {(novoServico.politicaExecucao === 'terceirizado' || novoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Preço quando terceirizo (R$) *
                    </label>
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={novoServico.precoTerceirizado}
                      onChange={(e) => setNovoServico({...novoServico, precoTerceirizado: parseFloat(e.target.value) || 0})}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                      placeholder="Ex: 450.00"
                      required={novoServico.politicaExecucao === 'terceirizado'}
                    />
                    <p className="text-sm text-gray-500 mt-1">
                      Valor que você paga ao terceirizado
                    </p>
                  </div>
                )}
              </div>

              {/* Seção: Prazos */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Tempo médio quando executo */}
                {(novoServico.politicaExecucao === 'proprio' || novoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Tempo quando executo (dias)
                    </label>
                    <input
                      type="number"
                      min="1"
                      value={novoServico.tempoMedioDias}
                      onChange={(e) => setNovoServico({...novoServico, tempoMedioDias: parseInt(e.target.value) || 0})}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                      placeholder="Ex: 7"
                    />
                  </div>
                )}

                {/* Prazo quando terceirizo */}
                {(novoServico.politicaExecucao === 'terceirizado' || novoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Prazo quando terceirizo (dias)
                    </label>
                    <input
                      type="number"
                      min="1"
                      value={novoServico.prazoTerceirizadoDias}
                      onChange={(e) => setNovoServico({...novoServico, prazoTerceirizadoDias: parseInt(e.target.value) || 0})}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                      placeholder="Ex: 10"
                    />
                    <p className="text-sm text-gray-500 mt-1">
                      Prazo estimado do terceirizado
                    </p>
                  </div>
                )}
              </div>

              {/* Terceirizado Preferido (se aplicável) */}
              {(novoServico.politicaExecucao === 'terceirizado' || novoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Terceirizado Preferido (opcional)
                  </label>
                  <input
                    type="number"
                    value={novoServico.terceirizadoPreferidoId || ''}
                    onChange={(e) => setNovoServico({...novoServico, terceirizadoPreferidoId: parseInt(e.target.value) || undefined})}
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="ID do protético preferido"
                  />
                  <p className="text-sm text-gray-500 mt-1">
                    ID do protético que você prefere para terceirizar
                  </p>
                </div>
              )}

              {/* Observações */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Observações sobre terceirização
                </label>
                <textarea
                  value={novoServico.observacoesTerceirizacao}
                  onChange={(e) => setNovoServico({...novoServico, observacoesTerceirizacao: e.target.value})}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  rows={3}
                  placeholder="Instruções, preferências, ou condições especiais para terceirização..."
                />
              </div>
            </div>
            
            <div className="flex gap-3 mt-8">
              <button
                onClick={() => setMostrarModal(false)}
                className="flex-1 px-4 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
              >
                Cancelar
              </button>
              <button
                onClick={adicionarServico}
                className="flex-1 px-4 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium"
              >
                Adicionar Serviço
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal para Editar Serviço */}
      {editandoServico && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold text-gray-800">Editar Serviço</h3>
              <button
                onClick={() => setEditandoServico(null)}
                className="text-gray-400 hover:text-gray-600"
              >
                ✕
              </button>
            </div>
            
            <div className="space-y-6">
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="font-medium text-gray-800">
                  {formatarTipoServico(editandoServico.tipoServico)}
                </p>
                <p className="text-sm text-gray-600 mt-1">{editandoServico.nomeServico}</p>
              </div>
              
              {/* Política de Execução */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-3">
                  Como você trabalha com este serviço? *
                </label>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                  {(['proprio', 'terceirizado', 'proprio_ou_terceirizado', 'nao_oferecido'] as const).map((politica) => {
                    const info = getPoliticaInfo(politica)
                    return (
                      <label key={politica} className={`cursor-pointer border-2 rounded-xl p-4 flex items-center gap-3 ${editandoServico.politicaExecucao === politica ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300'}`}>
                        <input
                          type="radio"
                          name="editPoliticaExecucao"
                          value={politica}
                          checked={editandoServico.politicaExecucao === politica}
                          onChange={(e) => setEditandoServico({
                            ...editandoServico,
                            politicaExecucao: e.target.value as any
                          })}
                          className="text-blue-600"
                        />
                        <div>
                          <div className="font-medium">{info.icon} {info.label}</div>
                        </div>
                      </label>
                    )
                  })}
                </div>
              </div>

              {/* Seção: Preços */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Preço quando executo */}
                {(editandoServico.politicaExecucao === 'proprio' || editandoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Preço quando executo (R$)
                    </label>
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={editandoServico.preco}
                      onChange={(e) => setEditandoServico({
                        ...editandoServico,
                        preco: parseFloat(e.target.value) || 0
                      })}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                )}

                {/* Preço quando terceirizo */}
                {(editandoServico.politicaExecucao === 'terceirizado' || editandoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Preço quando terceirizo (R$)
                    </label>
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={editandoServico.precoTerceirizado || ''}
                      onChange={(e) => setEditandoServico({
                        ...editandoServico,
                        precoTerceirizado: parseFloat(e.target.value) || undefined
                      })}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                )}
              </div>

              {/* Seção: Prazos */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Tempo médio quando executo */}
                {(editandoServico.politicaExecucao === 'proprio' || editandoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Tempo quando executo (dias)
                    </label>
                    <input
                      type="number"
                      min="1"
                      value={editandoServico.tempoMedioDias}
                      onChange={(e) => setEditandoServico({
                        ...editandoServico,
                        tempoMedioDias: parseInt(e.target.value) || 0
                      })}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                )}

                {/* Prazo quando terceirizo */}
                {(editandoServico.politicaExecucao === 'terceirizado' || editandoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Prazo quando terceirizo (dias)
                    </label>
                    <input
                      type="number"
                      min="1"
                      value={editandoServico.prazoTerceirizadoDias || ''}
                      onChange={(e) => setEditandoServico({
                        ...editandoServico,
                        prazoTerceirizadoDias: parseInt(e.target.value) || undefined
                      })}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                )}
              </div>

              {/* Terceirizado Preferido */}
              {(editandoServico.politicaExecucao === 'terceirizado' || editandoServico.politicaExecucao === 'proprio_ou_terceirizado') && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Terceirizado Preferido (opcional)
                  </label>
                  <input
                    type="number"
                    value={editandoServico.terceirizadoPreferidoId || ''}
                    onChange={(e) => setEditandoServico({
                      ...editandoServico,
                      terceirizadoPreferidoId: parseInt(e.target.value) || undefined
                    })}
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="ID do protético preferido"
                  />
                </div>
              )}

              {/* Observações */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Observações
                </label>
                <textarea
                  value={editandoServico.descricao || ''}
                  onChange={(e) => setEditandoServico({
                    ...editandoServico,
                    descricao: e.target.value
                  })}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  rows={2}
                  placeholder="Descrição do serviço..."
                />
              </div>

              {/* Observações de Terceirização */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Observações sobre terceirização
                </label>
                <textarea
                  value={editandoServico.observacoesTerceirizacao || ''}
                  onChange={(e) => setEditandoServico({
                    ...editandoServico,
                    observacoesTerceirizacao: e.target.value
                  })}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  rows={3}
                  placeholder="Instruções especiais para terceirização..."
                />
              </div>

              {/* Status Ativo */}
              <div className="bg-gray-50 p-4 rounded-lg">
                <label className="flex items-center gap-3">
                  <input
                    type="checkbox"
                    className="w-5 h-5 text-blue-600 rounded focus:ring-blue-500"
                    checked={editandoServico.ativo}
                    onChange={(e) => setEditandoServico({
                      ...editandoServico,
                      ativo: e.target.checked
                    })}
                  />
                  <div>
                    <span className="font-medium text-gray-800">Serviço Ativo</span>
                    <p className="text-sm text-gray-600">
                      Este serviço estará disponível para pedidos
                    </p>
                  </div>
                </label>
              </div>
            </div>
            
            <div className="flex gap-3 mt-8">
              <button
                onClick={() => setEditandoServico(null)}
                className="flex-1 px-4 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
              >
                Cancelar
              </button>
              <button
                onClick={() => {
                  atualizarServico(editandoServico)
                  setEditandoServico(null)
                }}
                className="flex-1 px-4 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium"
              >
                Salvar Alterações
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}