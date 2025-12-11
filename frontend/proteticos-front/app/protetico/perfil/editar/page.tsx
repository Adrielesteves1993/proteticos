"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

interface Protetico {
  id: number;
  nome: string;
  email: string;
  registroProfissional: string;
  especializacao: string;
  valorHora: number;
  capacidadePedidosSimultaneos: number;
  aceitaTerceirizacao: boolean;
}

export default function EditarPerfilPage() {
  const router = useRouter();
  const [form, setForm] = useState<Protetico | null>(null);

  useEffect(() => {
    carregarProtetico();
  }, []);

  const carregarProtetico = async () => {
    try {
      const usuarioJSON = localStorage.getItem("usuario");
      if (!usuarioJSON) return;

      const usuario = JSON.parse(usuarioJSON);

      const res = await fetch(
        `http://localhost:8080/api/proteticos/${usuario.id}`
      );

      const data = await res.json();
      setForm(data);
    } catch (err) {
      console.error(err);
    }
  };

  const salvar = async () => {
    if (!form) return;

    const res = await fetch(
      `http://localhost:8080/api/proteticos/${form.id}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      }
    );

    if (res.ok) {
      alert("Perfil atualizado!");
      router.push("/protetico/perfil");
    } else {
      alert("Erro ao atualizar perfil");
    }
  };

  if (!form) return <div className="p-8 text-xl">Carregando...</div>;

  return (
    <div className="p-8 space-y-6 max-w-3xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-800">Editar Perfil</h1>

      {/* FORMULÁRIO */}
      <div className="space-y-4 bg-white p-6 rounded-2xl shadow">
        <input
          className="w-full p-2 border rounded"
          value={form.nome}
          onChange={(e) => setForm({ ...form, nome: e.target.value })}
          placeholder="Nome"
        />

        <input
          className="w-full p-2 border rounded"
          value={form.registroProfissional}
          onChange={(e) =>
            setForm({ ...form, registroProfissional: e.target.value })
          }
          placeholder="Registro"
        />

        <input
          className="w-full p-2 border rounded"
          value={form.especializacao}
          onChange={(e) =>
            setForm({ ...form, especializacao: e.target.value })
          }
          placeholder="Especialização"
        />

        <input
          type="number"
          className="w-full p-2 border rounded"
          value={form.valorHora}
          onChange={(e) =>
            setForm({ ...form, valorHora: Number(e.target.value) })
          }
          placeholder="Valor por Hora"
        />

        <input
          type="number"
          className="w-full p-2 border rounded"
          value={form.capacidadePedidosSimultaneos}
          onChange={(e) =>
            setForm({
              ...form,
              capacidadePedidosSimultaneos: Number(e.target.value),
            })
          }
          placeholder="Capacidade Máxima de Pedidos"
        />

        <label className="flex items-center gap-2">
          <input
            type="checkbox"
            checked={form.aceitaTerceirizacao}
            onChange={(e) =>
              setForm({ ...form, aceitaTerceirizacao: e.target.checked })
            }
          />
          Aceita terceirização
        </label>

        <button
          onClick={salvar}
          className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
        >
          Salvar Alterações
        </button>
      </div>
    </div>
  );
}
