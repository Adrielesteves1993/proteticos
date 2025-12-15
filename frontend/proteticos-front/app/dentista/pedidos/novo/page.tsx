// app/dentista/pedidos/novo/page.tsx
"use client"

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'

interface Protetico {
  id: number
  nome: string
  especializacao: string
  registroProfissional: string
}

interface ServicoProtetico {
  id: number
  tipoServico: string
  tipoServicoNome: string
  preco: number
  tempoMedioDias: number
  descricao: string
  ativo: boolean
}

export default function NovoPedido() {
  const router = useRouter()
  const [proteticos, setProteticos] = useState<Protetico[]>([])
  const [servicosDisponiveis, setServicosDisponiveis] = useState<ServicoProtetico[]>([])
  const [servicoSelecionado, setServicoSelecionado] = useState<ServicoProtetico | null>(null)
  const [carregando, setCarregando] = useState(false)
  const [carregandoServicos, setCarregandoServicos] = useState(false)
  const [dados, setDados] = useState({
    proteticoId: '',
    tipoServico: '',
    informacoesDetalhadas: '',
    dataPrevistaEntrega: '',
    valorCobrado: '',
    prazoEstimado: ''
  })

  useEffect(() => {
    carregarProteticos()
  }, [])

  const carregarProteticos = async () => {
    try {
      console.log('🔄 Buscando protéticos...')
      const response = await fetch('http://localhost:8080/api/proteticos/ativos')
      if (response.ok) {
        const data = await response.json()
        console.log(`✅ ${data.length} protéticos carregados`)
        setProteticos(data)
      } else {
        console.error('❌ Erro ao carregar protéticos:', response.status)
      }
    } catch (error) {
      console.error('Erro ao carregar protéticos:', error)
    }
  }

  const carregarServicosDoProtetico = async (proteticoId: string) => {
    if (!proteticoId) {
      setServicosDisponiveis([])
      setServicoSelecionado(null)
      return
    }

    try {
      setCarregandoServicos(true)
      console.log(`🔄 Buscando serviços do protético ${proteticoId}...`)
      
      const response = await fetch(`http://localhost:8080/api/proteticos/${proteticoId}/servicos/ativos`)
      if (response.ok) {
        const servicos = await response.json()
        console.log(`✅ ${servicos.length} serviços ativos carregados:`, servicos)
        
        // Formata os serviços para exibição
        const servicosFormatados = servicos.map((servico: any) => ({
          id: servico.id,
          tipoServico: servico.tipoServico,
          tipoServicoNome: formatarNomeServico(servico.tipoServico),
          preco: servico.preco,
          tempoMedioDias: servico.tempoMedioDias || 7,
          descricao: servico.descricao || '',
          ativo: servico.ativo
        }))
        
        setServicosDisponiveis(servicosFormatados)
        
        // Limpa seleção de serviço anterior
        setServicoSelecionado(null)
        setDados(prev => ({
          ...prev,
          tipoServico: '',
          valorCobrado: '',
          prazoEstimado: ''
        }))
      } else {
        console.error('❌ Erro ao carregar serviços:', response.status)
      }
    } catch (error) {
      console.error('Erro ao carregar serviços:', error)
    } finally {
      setCarregandoServicos(false)
    }
  }

  const handleSelecionarProtetico = (proteticoId: string) => {
    setDados(prev => ({ ...prev, proteticoId }))
    carregarServicosDoProtetico(proteticoId)
  }

  const handleSelecionarServico = (tipoServico: string) => {
    const servico = servicosDisponiveis.find(s => s.tipoServico === tipoServico)
    
    if (servico) {
      setServicoSelecionado(servico)
      
      // Preenche automaticamente o preço e prazo
      setDados(prev => ({
        ...prev,
        tipoServico: servico.tipoServico,
        valorCobrado: servico.preco.toString(),
        prazoEstimado: servico.tempoMedioDias.toString()
      }))
      
      // Calcula data prevista baseada no prazo
      if (servico.tempoMedioDias) {
        const dataPrevista = new Date()
        dataPrevista.setDate(dataPrevista.getDate() + servico.tempoMedioDias)
        setDados(prev => ({
          ...prev,
          dataPrevistaEntrega: dataPrevista.toISOString().split('T')[0]
        }))
      }
    }
  }

  const formatarNomeServico = (tipo: string) => {
    const tipos: { [key: string]: string } = {
      'COROA': 'Coroa',
      'PONTE_FIXA': 'Ponte Fixa',
      'PROTESE_TOTAL': 'Prótese Total',
      'PROTESE_PARCIAL': 'Prótese Parcial',
      'PROVISORIO': 'Provisório',
      'ZIRCONIA': 'Zircônia',
      'RESINA': 'Resina',
      'IMPLANTE': 'Implante',
      'ORTODONTIA': 'Ortodontia',
      'OUTRO': 'Outro'
    }
    return tipos[tipo] || tipo.replace(/_/g, ' ')
  }

  const formatarValor = (valor: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor)
  }

  const calcularDataPrevista = (dias: number) => {
    const data = new Date()
    data.setDate(data.getDate() + dias)
    return data.toLocaleDateString('pt-BR')
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!servicoSelecionado) {
      alert('Selecione um serviço do protético')
      return
    }

    setCarregando(true)

    try {
      const usuario = JSON.parse(localStorage.getItem('usuario') || '{}')
      
      const pedidoData = {
        proteticoId: parseInt(dados.proteticoId),
        tipoServico: dados.tipoServico,
        informacoesDetalhadas: dados.informacoesDetalhadas,
        dataPrevistaEntrega: dados.dataPrevistaEntrega || null,
        valorCobrado: dados.valorCobrado ? parseFloat(dados.valorCobrado) : servicoSelecionado.preco,
        prazoEstimadoDias: servicoSelecionado.tempoMedioDias,
        dentistaId: usuario.id
      }

      console.log('📤 Enviando pedido:', pedidoData)

      const response = await fetch('http://localhost:8080/api/pedidos/novo', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(pedidoData)
      })

      if (response.ok) {
        alert('Pedido criado com sucesso!')
        router.push('/dentista/pedidos')
      } else {
        const erro = await response.text()
        console.error('❌ Erro na API:', erro)
        alert(`Erro ao criar pedido: ${erro}`)
      }
    } catch (error) {
      console.error('Erro ao criar pedido:', error)
      alert('Erro ao conectar com o servidor')
    } finally {
      setCarregando(false)
    }
  }

  const proteticoSelecionado = proteticos.find(p => p.id.toString() === dados.proteticoId)

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
          <p className="text-gray-600">Selecione o protético e serviço desejado</p>
        </div>

        {/* Formulário */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Passo 1: Selecionar Protético */}
            <div className="bg-blue-50 rounded-lg p-4 mb-6">
              <h2 className="font-semibold text-blue-900 mb-3 text-lg">1️⃣ Escolha o Protético</h2>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Protético Responsável *
                </label>
                <select
                  value={dados.proteticoId}
                  onChange={(e) => handleSelecionarProtetico(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="">Selecione um protético</option>
                  {proteticos.map((protetico) => (
                    <option key={protetico.id} value={protetico.id}>
                      {protetico.nome} - {protetico.especializacao} (Registro: {protetico.registroProfissional})
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Passo 2: Selecionar Serviço (aparece só depois de selecionar protético) */}
            {dados.proteticoId && (
              <div className="bg-green-50 rounded-lg p-4 mb-6">
                <h2 className="font-semibold text-green-900 mb-3 text-lg">2️⃣ Escolha o Serviço</h2>
                
                {carregandoServicos ? (
                  <div className="text-center py-4">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600 mx-auto"></div>
                    <p className="text-gray-600 mt-2">Carregando serviços disponíveis...</p>
                  </div>
                ) : servicosDisponiveis.length === 0 ? (
                  <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                    <p className="text-yellow-800">
                      {proteticoSelecionado?.nome} ainda não cadastrou serviços disponíveis.
                    </p>
                    <p className="text-sm text-yellow-600 mt-1">
                      Entre em contato diretamente com o protético ou escolha outro profissional.
                    </p>
                  </div>
                ) : (
                  <>
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Tipo de Serviço *
                      </label>
                      <select
                        value={dados.tipoServico}
                        onChange={(e) => handleSelecionarServico(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                        required
                      >
                        <option value="">Selecione o serviço</option>
                        {servicosDisponiveis.map((servico) => (
                          <option key={servico.id} value={servico.tipoServico}>
                            {servico.tipoServicoNome} - {formatarValor(servico.preco)} (Prazo: {servico.tempoMedioDias} dias)
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Resumo do Serviço Selecionado */}
                    {servicoSelecionado && (
                      <div className="bg-white border border-green-200 rounded-lg p-4 mt-4">
                        <h3 className="font-semibold text-green-800 mb-3">✅ Serviço Selecionado</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                          <div>
                            <p className="text-sm text-gray-600">Serviço:</p>
                            <p className="font-semibold text-gray-800">{servicoSelecionado.tipoServicoNome}</p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-600">Preço definido:</p>
                            <p className="font-semibold text-green-700">{formatarValor(servicoSelecionado.preco)}</p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-600">Prazo estimado:</p>
                            <p className="font-semibold text-blue-700">{servicoSelecionado.tempoMedioDias} dias</p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-600">Previsão de entrega:</p>
                            <p className="font-semibold text-blue-700">
                              {calcularDataPrevista(servicoSelecionado.tempoMedioDias)}
                            </p>
                          </div>
                        </div>
                        {servicoSelecionado.descricao && (
                          <div className="mt-3 pt-3 border-t border-gray-100">
                            <p className="text-sm text-gray-600">Descrição:</p>
                            <p className="text-gray-800">{servicoSelecionado.descricao}</p>
                          </div>
                        )}
                      </div>
                    )}
                  </>
                )}
              </div>
            )}

            {/* Passo 3: Detalhes do Pedido (só aparece depois de selecionar serviço) */}
            {servicoSelecionado && (
              <div className="bg-purple-50 rounded-lg p-4 mb-6">
                <h2 className="font-semibold text-purple-900 mb-3 text-lg">3️⃣ Detalhes do Pedido</h2>
                
                {/* Data Prevista (editável, mas com sugestão) */}
                <div className="mb-4">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Data Prevista para Entrega *
                  </label>
                  <input
                    type="date"
                    value={dados.dataPrevistaEntrega}
                    onChange={(e) => setDados({...dados, dataPrevistaEntrega: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                    min={new Date().toISOString().split('T')[0]}
                    required
                  />
                  <p className="text-sm text-gray-500 mt-1">
                    Sugestão baseada no prazo do protético: {calcularDataPrevista(servicoSelecionado.tempoMedioDias)}
                  </p>
                </div>

                {/* Valor (editável, mas com valor sugerido) */}
                <div className="mb-4">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Valor Cobrado (R$) *
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2 text-gray-500">R$</span>
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      value={dados.valorCobrado}
                      onChange={(e) => setDados({...dados, valorCobrado: e.target.value})}
                      className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                      placeholder="0,00"
                      required
                    />
                  </div>
                  <p className="text-sm text-gray-500 mt-1">
                    Valor sugerido pelo protético: {formatarValor(servicoSelecionado.preco)}
                  </p>
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
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                    placeholder="Descreva detalhes do caso, cor desejada, material, dentes envolvidos, observações importantes..."
                    required
                  />
                </div>
              </div>
            )}

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
                disabled={carregando || !servicoSelecionado}
                className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-2 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {carregando ? 'Criando Pedido...' : 'Criar Pedido'}
              </button>
            </div>
          </form>
        </div>

        {/* Informações de Ajuda */}
        <div className="mt-6 bg-blue-50 rounded-lg p-4">
          <h3 className="font-semibold text-blue-900 mb-2">💡 Como funciona agora:</h3>
          <ul className="text-sm text-blue-800 space-y-1">
            <li>• <strong>1º Passo:</strong> Selecione o protético - o sistema busca seus serviços cadastrados</li>
            <li>• <strong>2º Passo:</strong> Escolha o serviço - preço e prazo já estão definidos pelo protético</li>
            <li>• <strong>3º Passo:</strong> Ajuste data e valor se necessário e adicione os detalhes do caso</li>
            <li>• O protético será notificado automaticamente sobre o novo pedido</li>
          </ul>
        </div>

        {/* Debug Info */}
        <div className="mt-4 text-xs text-gray-500">
          <details>
            <summary className="cursor-pointer">Informações técnicas (debug)</summary>
            <div className="mt-2 space-y-1 bg-gray-100 p-2 rounded">
              <div>Protético selecionado: {dados.proteticoId || 'Nenhum'}</div>
              <div>Serviços disponíveis: {servicosDisponiveis.length}</div>
              <div>Serviço selecionado: {servicoSelecionado?.tipoServicoNome || 'Nenhum'}</div>
            </div>
          </details>
        </div>
      </div>
    </div>
  )
}