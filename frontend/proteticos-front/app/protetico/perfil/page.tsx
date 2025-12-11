// app/protetico/dashboard/perfil/page.tsx
"use client"

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'

// Tipos
interface Protetico {
  id: number
  nome: string
  email: string
  tipo: string
  registroProfissional: string
  especializacao: string
  aceitaTerceirizacao: boolean
  capacidadePedidosSimultaneos: number
  valorHora: number | null
  dataCriacao: string
  ativo: boolean
}

interface ServicoProtetico {
  id: number
  tipoServico: string
  descricao: string
  preco: number
  tempoMedioProducao: number // em DIAS - nome interno no frontend
  ativo: boolean
  proteticoId: number
}

interface TipoServicoDisponivel {
  codigo: string
  nome: string
  descricao: string
  categoria: string
}

export default function PerfilPage() {
  const router = useRouter()
  const [protetico, setProtetico] = useState<Protetico | null>(null)
  const [servicos, setServicos] = useState<ServicoProtetico[]>([])
  const [tiposServicoDisponiveis, setTiposServicoDisponiveis] = useState<TipoServicoDisponivel[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [mostrarModal, setMostrarModal] = useState(false)
  const [novoServico, setNovoServico] = useState({
    tipoServico: '',
    preco: 0,
    tempoMedioProducao: 7 // 7 DIAS padrão
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
      console.log('🔍 Usuário logado:', usuario)
      
      // 2. Valida se é protético
      const tipoUsuario = String(usuario.tipo || '').toUpperCase()
      if (!tipoUsuario.includes('PROTETICO')) {
        throw new Error('Apenas protéticos podem acessar esta página.')
      }
      
      // 3. Carrega dados do protético da API
      console.log(`🔄 Buscando protético ID ${usuario.id}...`)
      const proteticoRes = await fetch(`http://localhost:8080/api/proteticos/${usuario.id}`)
      
      if (!proteticoRes.ok) {
        throw new Error(`Erro ${proteticoRes.status} ao buscar protético`)
      }
      
      const proteticoData: Protetico = await proteticoRes.json()
      setProtetico(proteticoData)
      
      // 4. Carrega serviços do protético
      await carregarServicos(proteticoData.id)
      
      // 5. Carrega tipos de serviço disponíveis
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
      console.log(`🔄 Buscando serviços do protético ${proteticoId}...`)
      const servicosRes = await fetch(`http://localhost:8080/api/proteticos/${proteticoId}/servicos`)
      
      if (servicosRes.ok) {
        const servicosData = await servicosRes.json()
        console.log('✅ Serviços recebidos da API:', servicosData)
        
        // Mapeia os dados recebidos para a interface do frontend
        const servicosMapeados = servicosData.map((item: any) => ({
          id: item.id,
          tipoServico: item.tipoServico,
          descricao: item.descricao || '',
          preco: item.preco,
          tempoMedioProducao: item.tempoMedioDias || 0, // Backend retorna tempoMedioDias
          ativo: item.ativo,
          proteticoId: item.proteticoId
        }))
        
        setServicos(Array.isArray(servicosMapeados) ? servicosMapeados : [])
        console.log(`✅ ${servicosMapeados.length} serviços carregados`)
      }
    } catch (error) {
      console.error('❌ Erro ao carregar serviços:', error)
    }
  }

  const carregarTiposServico = async () => {
    try {
      // Endpoint que retorna todos os tipos de serviço disponíveis
      const tiposRes = await fetch('http://localhost:8080/api/servicos/tipos')
      
      if (tiposRes.ok) {
        const tiposData = await tiposRes.json()
        setTiposServicoDisponiveis(Array.isArray(tiposData) ? tiposData : [])
      } else {
        // Fallback com tipos padrão
        setTiposServicoDisponiveis([
          { codigo: 'COROA', nome: 'Coroa', descricao: 'Coroa dentária', categoria: 'RESTAURACAO' },
          { codigo: 'PONTE_FIXA', nome: 'Ponte Fixa', descricao: 'Ponte fixa', categoria: 'RESTAURACAO' },
          { codigo: 'PROTESE_TOTAL', nome: 'Prótese Total', descricao: 'Prótese dentária completa', categoria: 'PROTESE' },
          { codigo: 'PROTESE_PARCIAL', nome: 'Prótese Parcial', descricao: 'Prótese dentária parcial', categoria: 'PROTESE' },
          { codigo: 'FACETA', nome: 'Faceta', descricao: 'Faceta dental', categoria: 'ESTETICA' },
          { codigo: 'IMPLANTE', nome: 'Implante', descricao: 'Implante dentário', categoria: 'IMPLANTE' },
          { codigo: 'APARELHO_ORTO', nome: 'Aparelho Ortodôntico', descricao: 'Aparelho fixo ou móvel', categoria: 'ORTODONTIA' }
        ])
      }
    } catch (error) {
      console.error('❌ Erro ao carregar tipos de serviço:', error)
    }
  }

  const adicionarServico = async () => {
    if (!protetico || !novoServico.tipoServico || novoServico.preco <= 0) {
      alert('Preencha todos os campos obrigatórios')
      return
    }

    try {
      console.log('🔄 Adicionando serviço:', novoServico)
      
      // CORREÇÃO AQUI: Enviar tempoMedioDias em vez de tempoMedioProducao
      const payload = {
        tipoServico: novoServico.tipoServico,
        preco: novoServico.preco,
        tempoMedioDias: novoServico.tempoMedioProducao, // NOME CORRETO PARA O BACKEND
        descricao: '',
        ativo: true
      }
      
      console.log('📤 Enviando payload:', payload)
      
      const response = await fetch(`http://localhost:8080/api/proteticos/${protetico.id}/servicos`, {
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

      const servicoCriado = await response.json()
      console.log('✅ Serviço adicionado:', servicoCriado)
      
      // Atualiza a lista
      await carregarServicos(protetico.id)
      
      // Limpa o formulário
      setNovoServico({
        tipoServico: '',
        preco: 0,
        tempoMedioProducao: 7
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
      console.log('🔄 Atualizando serviço:', servico)
      
      // CORREÇÃO AQUI: Usar o novo endpoint PUT /{tipoServico} que criamos
      const payload = {
        preco: servico.preco,
        tempoMedioDias: servico.tempoMedioProducao, // NOME CORRETO
        descricao: servico.descricao || ''
      }
      
      console.log('📤 Enviando payload de atualização:', payload)
      
      const response = await fetch(`http://localhost:8080/api/proteticos/${protetico?.id}/servicos/${servico.tipoServico}`, {
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

      console.log('✅ Serviço atualizado')
      await carregarServicos(protetico!.id)
      
    } catch (error: any) {
      console.error('❌ Erro ao atualizar serviço:', error)
      alert(`Erro: ${error.message}`)
    }
  }

  const toggleServicoStatus = async (servico: ServicoProtetico) => {
    try {
      const novoStatus = !servico.ativo
      console.log(`🔄 ${novoStatus ? 'Ativando' : 'Desativando'} serviço:`, servico.tipoServico)
      
      const response = await fetch(`http://localhost:8080/api/proteticos/${protetico?.id}/servicos/${servico.tipoServico}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({
          ativo: novoStatus
        })
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Erro ${response.status}: ${errorText}`)
      }

      console.log('✅ Status do serviço atualizado')
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
      console.log(`🔄 Removendo serviço: ${tipoServico}`)
      
      const response = await fetch(`http://localhost:8080/api/proteticos/${protetico?.id}/servicos/${tipoServico}`, {
        method: 'DELETE'
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Erro ${response.status}: ${errorText}`)
      }

      console.log('✅ Serviço removido')
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

  const getTiposServicoNaoAdicionados = () => {
    const tiposAdicionados = servicos.map(s => s.tipoServico)
    return tiposServicoDisponiveis.filter(tipo => 
      !tiposAdicionados.includes(tipo.codigo)
    )
  }

  // Estatísticas simplificadas
  const servicosAtivos = servicos.filter(s => s.ativo).length
  const valorMedio = servicos.length > 0 
    ? servicos.reduce((sum, s) => sum + s.preco, 0) / servicos.length 
    : 0

  if (carregando) {
    return (
      <div className="p-8 space-y-8">
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
      <div className="p-8 space-y-8">
        <h1 className="text-3xl font-bold text-gray-800">Erro</h1>
        <div className="bg-red-50 border border-red-200 rounded-lg p-6">
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

  if (!protetico) return null

  return (
    <div className="p-4 md:p-8 space-y-8">
      {/* Cabeçalho SIMPLIFICADO - sem especialização */}
      <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-6 border border-blue-100">
        <div className="flex flex-col md:flex-row md:items-center justify-between">
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-gray-800">
              Olá, <span className="text-blue-600">{protetico.nome}</span>!
            </h1>
            <p className="text-gray-600 mt-2">
              Registro Profissional: {protetico.registroProfissional}
            </p>
          </div>
          <div className="mt-4 md:mt-0">
            <span className={`inline-flex px-3 py-1 rounded-full text-sm font-semibold ${
              protetico.ativo 
                ? 'bg-green-100 text-green-800 border border-green-200' 
                : 'bg-red-100 text-red-800 border border-red-200'
            }`}>
              {protetico.ativo ? '✅ ATIVO' : '❌ INATIVO'}
            </span>
          </div>
        </div>
      </div>

      {/* Dashboard Stats SIMPLIFICADO - só serviços e preço médio */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Card 1: Serviços Ativos */}
        <div className="bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-200 rounded-2xl p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-blue-700">Serviços Ativos</p>
              <p className="text-3xl font-bold text-blue-800 mt-2">{servicosAtivos}</p>
              <p className="text-sm text-blue-600 mt-1">de {servicos.length} total</p>
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
              <p className="text-3xl font-bold text-green-800 mt-2">{formatarValor(valorMedio)}</p>
              <p className="text-sm text-green-600 mt-1">por serviço</p>
            </div>
            <div className="text-green-400">
              <svg className="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      {/* Seção de Serviços */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-200 overflow-hidden">
        <div className="p-6 border-b border-gray-200">
          <div className="flex flex-col md:flex-row md:items-center justify-between">
            <div>
              <h2 className="text-xl font-bold text-gray-800">Meus Serviços</h2>
              <p className="text-gray-600 mt-1">
                Gerencie os serviços que você oferece e seus preços
              </p>
            </div>
            <button
              onClick={() => setMostrarModal(true)}
              className="mt-4 md:mt-0 px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-lg hover:from-green-600 hover:to-emerald-700 flex items-center gap-2 font-medium"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              Adicionar Serviço
            </button>
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
                return (
                  <div 
                    key={servico.id} 
                    className="bg-gray-50 border border-gray-200 rounded-xl p-4 hover:bg-gray-100 transition-colors"
                  >
                    <div className="flex flex-col md:flex-row md:items-center justify-between">
                      <div className="flex-1">
                        <div className="flex items-start gap-3">
                          <div className={`p-2 rounded-lg ${
                            servico.ativo ? 'bg-green-100 text-green-800' : 'bg-gray-200 text-gray-600'
                          }`}>
                            {servico.ativo ? '✓' : '✗'}
                          </div>
                          <div>
                            <h4 className="font-bold text-gray-800">
                              {tipoInfo?.nome || servico.tipoServico.replace(/_/g, ' ')}
                            </h4>
                            <p className="text-gray-600 text-sm mt-1">
                              {tipoInfo?.descricao || 'Serviço dental'}
                            </p>
                            <div className="flex flex-wrap gap-4 mt-3">
                              <div className="text-sm">
                                <span className="text-gray-500">Preço: </span>
                                <span className="font-bold text-green-700">{formatarValor(servico.preco)}</span>
                              </div>
                              <div className="text-sm">
                                <span className="text-gray-500">Tempo médio: </span>
                                <span className="font-bold text-blue-700">{servico.tempoMedioProducao} dias</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      
                      <div className="mt-4 md:mt-0 flex gap-2">
                        <button
                          onClick={() => setEditandoServico(servico)}
                          className="px-3 py-1 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 text-sm font-medium"
                        >
                          Editar Preço
                        </button>
                        <button
                          onClick={() => toggleServicoStatus(servico)}
                          className={`px-3 py-1 rounded-lg text-sm font-medium ${
                            servico.ativo
                              ? 'bg-yellow-100 text-yellow-700 hover:bg-yellow-200'
                              : 'bg-green-100 text-green-700 hover:bg-green-200'
                          }`}
                        >
                          {servico.ativo ? 'Desativar' : 'Ativar'}
                        </button>
                        <button
                          onClick={() => removerServico(servico.tipoServico)}
                          className="px-3 py-1 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 text-sm font-medium"
                        >
                          Remover
                        </button>
                      </div>
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>

        {/* Estatísticas simplificadas */}
        {servicos.length > 0 && (
          <div className="bg-gray-50 border-t border-gray-200 p-4">
            <div className="flex flex-wrap gap-4 text-sm">
              <div className="bg-white px-3 py-2 rounded-lg border">
                <span className="text-gray-500">Total de serviços: </span>
                <span className="font-bold text-gray-800">{servicos.length}</span>
              </div>
              <div className="bg-white px-3 py-2 rounded-lg border">
                <span className="text-gray-500">Ativos: </span>
                <span className="font-bold text-green-700">{servicos.filter(s => s.ativo).length}</span>
              </div>
              <div className="bg-white px-3 py-2 rounded-lg border">
                <span className="text-gray-500">Inativos: </span>
                <span className="font-bold text-red-700">{servicos.filter(s => !s.ativo).length}</span>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Modal para Adicionar Serviço */}
      {mostrarModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl max-w-md w-full p-6">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold text-gray-800">Adicionar Serviço</h3>
              <button
                onClick={() => setMostrarModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                ✕
              </button>
            </div>
            
            <div className="space-y-4">
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
                <p className="text-sm text-gray-500 mt-1">
                  Escolha o serviço que você deseja oferecer
                </p>
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Preço (R$) *
                </label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  value={novoServico.preco}
                  onChange={(e) => setNovoServico({...novoServico, preco: parseFloat(e.target.value)})}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Ex: 250.00"
                  required
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tempo Médio de Produção (dias)
                </label>
                <input
                  type="number"
                  min="1"
                  value={novoServico.tempoMedioProducao}
                  onChange={(e) => setNovoServico({...novoServico, tempoMedioProducao: parseInt(e.target.value)})}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Ex: 7"
                />
                <p className="text-sm text-gray-500 mt-1">
                  Tempo estimado para concluir este serviço em dias
                </p>
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

      {/* Modal para Editar Preço */}
      {editandoServico && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl max-w-md w-full p-6">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold text-gray-800">Editar Serviço</h3>
              <button
                onClick={() => setEditandoServico(null)}
                className="text-gray-400 hover:text-gray-600"
              >
                ✕
              </button>
            </div>
            
            <div className="space-y-4">
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="font-medium text-gray-800">
                  {editandoServico.tipoServico.replace(/_/g, ' ')}
                </p>
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Novo Preço (R$)
                </label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  value={editandoServico.preco}
                  onChange={(e) => setEditandoServico({
                    ...editandoServico,
                    preco: parseFloat(e.target.value)
                  })}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tempo Médio (dias)
                </label>
                <input
                  type="number"
                  min="1"
                  value={editandoServico.tempoMedioProducao}
                  onChange={(e) => setEditandoServico({
                    ...editandoServico,
                    tempoMedioProducao: parseInt(e.target.value)
                  })}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                />
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