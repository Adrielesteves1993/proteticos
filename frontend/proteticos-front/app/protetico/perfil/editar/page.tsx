"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

interface Protetico {
  id: number;
  nome: string;
  email: string;
  registroProfissional: string;
  especializacao: string;
  valorHora: number | null;
  capacidadePedidosSimultaneos: number;
  ativo: boolean;
  telefone?: string;
  endereco?: string;
  cidade?: string;
  estado?: string;
  cep?: string;
  dataNascimento?: string;
  avatarUrl?: string;
}

export default function EditarPerfilPage() {
  const router = useRouter();
  const [form, setForm] = useState<Protetico | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    carregarProtetico();
  }, []);

  const carregarProtetico = async () => {
    try {
      setCarregando(true);
      const usuarioJSON = localStorage.getItem("usuario");
      if (!usuarioJSON) {
        router.push("/login");
        return;
      }

      const usuario = JSON.parse(usuarioJSON);

      const res = await fetch(
        `http://localhost:8080/api/proteticos/${usuario.id}`
      );

      if (!res.ok) {
        throw new Error(`Erro ${res.status} ao carregar perfil`);
      }

      const data = await res.json();
      setForm(data);
    } catch (err) {
      console.error("Erro ao carregar perfil:", err);
      alert("Erro ao carregar dados do perfil");
    } finally {
      setCarregando(false);
    }
  };

  const salvar = async () => {
    if (!form) return;

    try {
      setSalvando(true);

      // Remove campos que não devem ser enviados ou que são calculados
      const dadosParaEnviar = {
        ...form,
        // Garante que os campos numéricos são números válidos
        valorHora: form.valorHora || null,
        capacidadePedidosSimultaneos: form.capacidadePedidosSimultaneos || 1,
      };

      const res = await fetch(
        `http://localhost:8080/api/proteticos/${form.id}`,
        {
          method: "PUT",
          headers: { 
            "Content-Type": "application/json",
            "Accept": "application/json"
          },
          body: JSON.stringify(dadosParaEnviar),
        }
      );

      if (res.ok) {
        // Atualiza o usuário no localStorage
        const usuarioJSON = localStorage.getItem("usuario");
        if (usuarioJSON) {
          const usuario = JSON.parse(usuarioJSON);
          usuario.nome = form.nome;
          usuario.email = form.email;
          localStorage.setItem("usuario", JSON.stringify(usuario));
        }

        alert("✅ Perfil atualizado com sucesso!");
        router.push("/protetico/dashboard/perfil");
      } else {
        const errorText = await res.text();
        throw new Error(`Erro ${res.status}: ${errorText}`);
      }
    } catch (error: any) {
      console.error("Erro ao salvar:", error);
      alert(`❌ Erro ao atualizar perfil: ${error.message}`);
    } finally {
      setSalvando(false);
    }
  };

  const handleInputChange = (field: keyof Protetico, value: any) => {
    if (!form) return;
    
    setForm({
      ...form,
      [field]: value
    });
  };

  if (carregando) {
    return (
      <div className="p-8 space-y-8 max-w-3xl mx-auto">
        <div className="animate-pulse space-y-6">
          <div className="h-10 bg-gray-200 rounded w-1/3"></div>
          <div className="bg-white p-6 rounded-2xl shadow space-y-4">
            {[1, 2, 3, 4, 5].map(i => (
              <div key={i} className="h-12 bg-gray-200 rounded"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (!form) {
    return (
      <div className="p-8 text-center">
        <div className="text-6xl mb-4">😕</div>
        <h2 className="text-xl font-semibold text-gray-800 mb-2">Perfil não encontrado</h2>
        <p className="text-gray-600 mb-4">Não foi possível carregar os dados do perfil</p>
        <button
          onClick={() => router.push("/protetico/dashboard/perfil")}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          Voltar para Perfil
        </button>
      </div>
    );
  }

  return (
    <div className="p-4 md:p-8 space-y-8 max-w-4xl mx-auto">
      {/* Cabeçalho */}
      <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-6 border border-blue-100">
        <div className="flex flex-col md:flex-row md:items-center justify-between">
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-gray-800">
              Editar Perfil
            </h1>
            <p className="text-gray-600 mt-2">
              Atualize suas informações profissionais
            </p>
          </div>
          <button
            onClick={() => router.push("/protetico/dashboard/perfil")}
            className="mt-4 md:mt-0 px-4 py-2 text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg font-medium"
          >
            ← Voltar
          </button>
        </div>
      </div>

      {/* FORMULÁRIO */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-200 overflow-hidden">
        <div className="p-6 border-b">
          <h2 className="text-lg font-semibold text-gray-800">Informações Pessoais</h2>
          <p className="text-gray-600 text-sm mt-1">Dados básicos de identificação</p>
        </div>

        <div className="p-6 space-y-6">
          {/* Grid de 2 colunas para desktop */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Nome */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nome Completo *
              </label>
              <input
                type="text"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                value={form.nome}
                onChange={(e) => handleInputChange("nome", e.target.value)}
                placeholder="Seu nome completo"
                required
              />
            </div>

            {/* Email */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Email *
              </label>
              <input
                type="email"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                value={form.email}
                onChange={(e) => handleInputChange("email", e.target.value)}
                placeholder="seu@email.com"
                required
              />
            </div>

            {/* Registro Profissional */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Registro Profissional (CRO) *
              </label>
              <input
                type="text"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                value={form.registroProfissional}
                onChange={(e) => handleInputChange("registroProfissional", e.target.value)}
                placeholder="Ex: CRO-12345"
                required
              />
            </div>

            {/* Telefone */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Telefone
              </label>
              <input
                type="tel"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                value={form.telefone || ""}
                onChange={(e) => handleInputChange("telefone", e.target.value)}
                placeholder="(11) 99999-9999"
              />
            </div>
          </div>

          {/* Especialização */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Especialização *
            </label>
            <input
              type="text"
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              value={form.especializacao}
              onChange={(e) => handleInputChange("especializacao", e.target.value)}
              placeholder="Ex: Prótese Total, Coroas, Implantes"
              required
            />
            <p className="text-sm text-gray-500 mt-1">
              Separe por vírgulas as áreas em que você é especialista
            </p>
          </div>

          {/* Informações de Endereço */}
          <div className="border-t pt-6">
            <h3 className="text-md font-medium text-gray-800 mb-4">Informações de Endereço</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Endereço
                </label>
                <input
                  type="text"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  value={form.endereco || ""}
                  onChange={(e) => handleInputChange("endereco", e.target.value)}
                  placeholder="Rua, número, complemento"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Cidade
                </label>
                <input
                  type="text"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  value={form.cidade || ""}
                  onChange={(e) => handleInputChange("cidade", e.target.value)}
                  placeholder="Cidade"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Estado
                </label>
                <input
                  type="text"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  value={form.estado || ""}
                  onChange={(e) => handleInputChange("estado", e.target.value)}
                  placeholder="UF"
                  maxLength={2}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  CEP
                </label>
                <input
                  type="text"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  value={form.cep || ""}
                  onChange={(e) => handleInputChange("cep", e.target.value)}
                  placeholder="00000-000"
                />
              </div>
            </div>
          </div>

          {/* Informações Profissionais */}
          <div className="border-t pt-6">
            <h3 className="text-md font-medium text-gray-800 mb-4">Informações Profissionais</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Valor por Hora (R$)
                </label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  value={form.valorHora || ""}
                  onChange={(e) => handleInputChange("valorHora", e.target.value ? parseFloat(e.target.value) : null)}
                  placeholder="Ex: 150.00"
                />
                <p className="text-sm text-gray-500 mt-1">
                  Valor cobrado por hora de trabalho (opcional)
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Capacidade Máxima de Pedidos *
                </label>
                <input
                  type="number"
                  min="1"
                  max="100"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  value={form.capacidadePedidosSimultaneos}
                  onChange={(e) => handleInputChange("capacidadePedidosSimultaneos", parseInt(e.target.value) || 1)}
                  placeholder="Ex: 10"
                  required
                />
                <p className="text-sm text-gray-500 mt-1">
                  Quantos pedidos consegue atender simultaneamente
                </p>
              </div>
            </div>
          </div>

          {/* Status */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <label className="flex items-center gap-3">
              <input
                type="checkbox"
                className="w-5 h-5 text-blue-600 rounded focus:ring-blue-500"
                checked={form.ativo}
                onChange={(e) => handleInputChange("ativo", e.target.checked)}
              />
              <div>
                <span className="font-medium text-gray-800">Perfil Ativo</span>
                <p className="text-sm text-gray-600">
                  Seu perfil será visível para dentistas e outros protéticos
                </p>
              </div>
            </label>
          </div>

          {/* Nota sobre Terceirização */}
          <div className="bg-blue-50 border border-blue-100 p-4 rounded-lg">
            <div className="flex items-start gap-3">
              <div className="text-blue-500 mt-1">ℹ️</div>
              <div>
                <p className="font-medium text-blue-800">Sobre Terceirização</p>
                <p className="text-sm text-blue-700 mt-1">
                  A configuração de terceirização agora é feita por serviço individual. 
                  Acesse a página de serviços para definir como trabalha com cada tipo de procedimento.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Botões de Ação */}
        <div className="p-6 border-t bg-gray-50 flex flex-col sm:flex-row gap-3">
          <button
            onClick={() => router.push("/protetico/dashboard/perfil")}
            className="flex-1 px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 font-medium"
          >
            Cancelar
          </button>
          <button
            onClick={salvar}
            disabled={salvando}
            className={`flex-1 px-6 py-3 rounded-lg font-medium ${
              salvando
                ? "bg-blue-400 text-white cursor-not-allowed"
                : "bg-gradient-to-r from-blue-500 to-indigo-600 text-white hover:from-blue-600 hover:to-indigo-700"
            }`}
          >
            {salvando ? (
              <span className="flex items-center justify-center gap-2">
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                Salvando...
              </span>
            ) : (
              "Salvar Alterações"
            )}
          </button>
        </div>
      </div>

      {/* Links Rápidos */}
      <div className="bg-white rounded-2xl shadow border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Configurações Adicionais</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <button
            onClick={() => router.push("/protetico/dashboard/perfil")}
            className="p-4 bg-gray-50 border border-gray-200 rounded-xl hover:bg-gray-100 text-left"
          >
            <div className="flex items-center gap-3">
              <div className="p-2 bg-blue-100 text-blue-700 rounded-lg">🛠️</div>
              <div>
                <p className="font-medium text-gray-800">Gerenciar Serviços</p>
                <p className="text-sm text-gray-600">Configure como trabalha com cada serviço</p>
              </div>
            </div>
          </button>
          
          <button
            onClick={() => router.push("/protetico/dashboard/perfil/terceirizacao")}
            className="p-4 bg-gray-50 border border-gray-200 rounded-xl hover:bg-gray-100 text-left"
          >
            <div className="flex items-center gap-3">
              <div className="p-2 bg-purple-100 text-purple-700 rounded-lg">🤝</div>
              <div>
                <p className="font-medium text-gray-800">Terceirização</p>
                <p className="text-sm text-gray-600">Configure serviços que pode repassar</p>
              </div>
            </div>
          </button>
        </div>
      </div>
    </div>
  );
}