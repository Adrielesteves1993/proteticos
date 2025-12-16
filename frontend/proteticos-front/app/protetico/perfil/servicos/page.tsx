"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

interface ServicoProtetico {
  id: number;
  tipoServico: string;
  nomeServico: string;
  preco: number;
  descricao: string;
  ativo: boolean;
  tempoMedioHoras: number;
  tempoMedioDias: number;
  
  // NOVOS CAMPOS para terceirização
  politicaExecucao: 'proprio' | 'terceirizado' | 'proprio_ou_terceirizado' | 'nao_oferecido';
  precoTerceirizado?: number;
  prazoTerceirizadoDias?: number;
  terceirizadoPreferidoId?: number;
  terceirizadoPreferidoNome?: string;
  observacoesTerceirizacao?: string;
}

export default function ServicosPage() {
  const router = useRouter();
  const [servicos, setServicos] = useState<ServicoProtetico[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [abaAtiva, setAbaAtiva] = useState<'ativos' | 'todos' | 'terceirizaveis'>('ativos');

  useEffect(() => {
    carregarServicos();
  }, []);

  const carregarServicos = async () => {
    try {
      const usuarioJSON = localStorage.getItem("usuario");
      if (!usuarioJSON) return;

      const usuario = JSON.parse(usuarioJSON);

      // Use o NOVO endpoint que inclui dados de terceirização
      const res = await fetch(
        `http://localhost:8080/api/servicos-protetico/protetico/${usuario.id}`
      );

      if (!res.ok) {
        throw new Error(`Erro ${res.status} ao carregar serviços`);
      }

      const data = await res.json();
      setServicos(data);
    } catch (err) {
      console.error("Erro ao carregar serviços:", err);
    } finally {
      setCarregando(false);
    }
  };

  const formatarPolitica = (politica: string) => {
    const politicas = {
      'proprio': { label: '🏭 Executo', color: 'bg-blue-100 text-blue-800' },
      'terceirizado': { label: '🤝 Terceirizo', color: 'bg-purple-100 text-purple-800' },
      'proprio_ou_terceirizado': { label: '🔄 Flexível', color: 'bg-green-100 text-green-800' },
      'nao_oferecido': { label: '❌ Não ofereço', color: 'bg-gray-100 text-gray-800' }
    };
    
    return politicas[politica as keyof typeof politicas] || { label: 'Desconhecido', color: 'bg-gray-100 text-gray-800' };
  };

  const formatarTipoServico = (tipo: string) => {
    return tipo
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, l => l.toUpperCase());
  };

  const filtrarServicos = () => {
    switch (abaAtiva) {
      case 'ativos':
        return servicos.filter(s => s.ativo);
      case 'terceirizaveis':
        return servicos.filter(s => 
          s.ativo && (s.politicaExecucao === 'terceirizado' || s.politicaExecucao === 'proprio_ou_terceirizado')
        );
      case 'todos':
      default:
        return servicos;
    }
  };

  const servicosFiltrados = filtrarServicos();

  if (carregando) {
    return (
      <div className="p-8 space-y-8">
        <div className="animate-pulse space-y-6">
          <div className="h-10 bg-gray-200 rounded w-1/4"></div>
          <div className="h-6 bg-gray-200 rounded w-1/2"></div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {[1, 2, 3].map(i => (
              <div key={i} className="h-48 bg-gray-200 rounded"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-4 md:p-8 space-y-8">
      {/* Cabeçalho */}
      <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-6 border border-blue-100">
        <div className="flex flex-col md:flex-row md:items-center justify-between">
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-gray-800">
              Meus Serviços
            </h1>
            <p className="text-gray-600 mt-2">
              Gerencie os serviços que você oferece e suas políticas de execução
            </p>
          </div>
          <button
            onClick={() => router.push("/protetico/dashboard/perfil")}
            className="mt-4 md:mt-0 px-5 py-2.5 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 font-medium shadow-md"
          >
            ➕ Novo Serviço
          </button>
        </div>
      </div>

      {/* Abas */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-200">
        <div className="border-b border-gray-200">
          <nav className="-mb-px flex space-x-4 px-6">
            <button
              onClick={() => setAbaAtiva('ativos')}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${abaAtiva === 'ativos'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
            >
              ✅ Ativos ({servicos.filter(s => s.ativo).length})
            </button>
            <button
              onClick={() => setAbaAtiva('terceirizaveis')}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${abaAtiva === 'terceirizaveis'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
            >
              🤝 Posso Terceirizar ({servicos.filter(s => 
                s.ativo && (s.politicaExecucao === 'terceirizado' || s.politicaExecucao === 'proprio_ou_terceirizado')
              ).length})
            </button>
            <button
              onClick={() => setAbaAtiva('todos')}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${abaAtiva === 'todos'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
            >
              📋 Todos ({servicos.length})
            </button>
          </nav>
        </div>

        {/* Estatísticas */}
        <div className="p-6 bg-gray-50 border-b">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-white p-4 rounded-xl border">
              <p className="text-sm text-gray-500">Total de Serviços</p>
              <p className="text-2xl font-bold text-gray-800 mt-1">{servicos.length}</p>
            </div>
            <div className="bg-white p-4 rounded-xl border">
              <p className="text-sm text-gray-500">Pode Terceirizar</p>
              <p className="text-2xl font-bold text-purple-600 mt-1">
                {servicos.filter(s => s.politicaExecucao === 'terceirizado' || s.politicaExecucao === 'proprio_ou_terceirizado').length}
              </p>
            </div>
            <div className="bg-white p-4 rounded-xl border">
              <p className="text-sm text-gray-500">Executo Próprio</p>
              <p className="text-2xl font-bold text-blue-600 mt-1">
                {servicos.filter(s => s.politicaExecucao === 'proprio' || s.politicaExecucao === 'proprio_ou_terceirizado').length}
              </p>
            </div>
          </div>
        </div>

        {/* Lista de Serviços */}
        <div className="p-6">
          {servicosFiltrados.length === 0 ? (
            <div className="text-center py-12">
              <div className="text-6xl mb-4">
                {abaAtiva === 'ativos' ? '📭' : 
                 abaAtiva === 'terceirizaveis' ? '🤷‍♂️' : '📋'}
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">
                {abaAtiva === 'ativos' ? 'Nenhum serviço ativo' : 
                 abaAtiva === 'terceirizaveis' ? 'Nenhum serviço pode ser terceirizado' : 
                 'Nenhum serviço cadastrado'}
              </h3>
              <p className="text-gray-600 mb-6">
                {abaAtiva === 'ativos' ? 'Ative algum serviço ou adicione um novo' : 
                 abaAtiva === 'terceirizaveis' ? 'Configure a política de execução dos seus serviços' : 
                 'Adicione serviços para começar a receber pedidos'}
              </p>
              <button
                onClick={() => router.push("/protetico/dashboard/perfil")}
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                Adicionar Serviço
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {servicosFiltrados.map((servico) => {
                const politica = formatarPolitica(servico.politicaExecucao);
                
                return (
                  <div
                    key={servico.id}
                    className="bg-white border border-gray-200 rounded-xl p-5 hover:shadow-lg transition-all duration-200 hover:border-blue-200"
                  >
                    {/* Cabeçalho do Card */}
                    <div className="flex justify-between items-start mb-4">
                      <div>
                        <div className="flex items-center gap-2">
                          <span className={`inline-flex px-3 py-1 text-xs font-semibold rounded-full ${politica.color}`}>
                            {politica.label}
                          </span>
                          {!servico.ativo && (
                            <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800">
                              Inativo
                            </span>
                          )}
                        </div>
                        <h2 className="text-xl font-bold text-gray-800 mt-2">
                          {formatarTipoServico(servico.tipoServico)}
                        </h2>
                        <p className="text-gray-600 text-sm mt-1">
                          {servico.nomeServico}
                        </p>
                      </div>
                    </div>

                    {/* Descrição */}
                    {servico.descricao && (
                      <p className="text-gray-600 text-sm mb-4 line-clamp-2">
                        {servico.descricao}
                      </p>
                    )}

                    {/* Informações de Preço */}
                    <div className="space-y-3 mb-4">
                      {/* Preço Principal */}
                      {(servico.politicaExecucao === 'proprio' || servico.politicaExecucao === 'proprio_ou_terceirizado') && (
                        <div className="bg-blue-50 p-3 rounded-lg">
                          <div className="flex justify-between items-center">
                            <div>
                              <p className="text-sm font-medium text-blue-700">🏭 Quando executo:</p>
                              <p className="font-bold text-blue-800 text-lg">R$ {servico.preco.toFixed(2)}</p>
                            </div>
                            <p className="text-sm text-blue-600">{servico.tempoMedioDias || servico.tempoMedioHoras / 24} dias</p>
                          </div>
                        </div>
                      )}

                      {/* Preço de Terceirização */}
                      {(servico.politicaExecucao === 'terceirizado' || servico.politicaExecucao === 'proprio_ou_terceirizado') && (
                        <div className="bg-purple-50 p-3 rounded-lg">
                          <div className="flex justify-between items-center">
                            <div>
                              <p className="text-sm font-medium text-purple-700">🤝 Quando terceirizo:</p>
                              <p className="font-bold text-purple-800 text-lg">
                                {servico.precoTerceirizado ? `R$ ${servico.precoTerceirizado.toFixed(2)}` : 'Não definido'}
                              </p>
                            </div>
                            <p className="text-sm text-purple-600">{servico.prazoTerceirizadoDias || '—'} dias</p>
                          </div>
                        </div>
                      )}

                      {/* Terceirizado Preferido */}
                      {servico.terceirizadoPreferidoNome && (
                        <div className="bg-green-50 p-3 rounded-lg">
                          <p className="text-sm font-medium text-green-700 mb-1">👤 Terceirizado Preferido:</p>
                          <p className="text-sm text-green-800">{servico.terceirizadoPreferidoNome}</p>
                        </div>
                      )}
                    </div>

                    {/* Observações */}
                    {servico.observacoesTerceirizacao && (
                      <div className="bg-yellow-50 p-3 rounded-lg mb-4">
                        <p className="text-sm font-medium text-yellow-700 mb-1">📝 Observações:</p>
                        <p className="text-sm text-yellow-800 line-clamp-2">{servico.observacoesTerceirizacao}</p>
                      </div>
                    )}

                    {/* Botões de Ação */}
                    <div className="flex justify-between items-center pt-4 border-t border-gray-100">
                      <div className="text-sm text-gray-500">
                        ID: {servico.id}
                      </div>
                      <div className="flex gap-2">
                        <button
                          onClick={() => router.push(`/protetico/dashboard/perfil#editar-${servico.id}`)}
                          className="px-3 py-1.5 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 text-sm font-medium"
                        >
                          Editar
                        </button>
                        <button
                          onClick={() => router.push(`/protetico/dashboard/perfil/terceirizacao`)}
                          className="px-3 py-1.5 bg-purple-100 text-purple-700 rounded-lg hover:bg-purple-200 text-sm font-medium"
                        >
                          Configurar
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>

      {/* Botões de Ação Globais */}
      <div className="flex flex-wrap gap-4">
        <button
          onClick={() => router.push("/protetico/dashboard/perfil/terceirizacao")}
          className="flex-1 md:flex-none px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg hover:from-purple-600 hover:to-pink-600 font-medium"
        >
          🤝 Configuração de Terceirização
        </button>
        <button
          onClick={() => router.push("/protetico/dashboard/perfil")}
          className="flex-1 md:flex-none px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-500 text-white rounded-lg hover:from-blue-600 hover:to-indigo-600 font-medium"
        >
          🛠️ Gerenciar Todos os Serviços
        </button>
        <button
          onClick={carregarServicos}
          className="px-6 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 font-medium"
        >
          🔄 Atualizar
        </button>
      </div>
    </div>
  );
}