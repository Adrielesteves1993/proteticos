"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

interface Servico {
  id: number;
  nomeServico: string;
  tipoServico: string;
  preco: number;
  descricao: string;
  ativo: boolean;
  tempoMedioHoras: number;
}

export default function ServicosPage() {
  const router = useRouter();
  const [servicos, setServicos] = useState<Servico[]>([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    carregarServicos();
  }, []);

  const carregarServicos = async () => {
    try {
      const usuarioJSON = localStorage.getItem("usuario");
      if (!usuarioJSON) return;

      const usuario = JSON.parse(usuarioJSON);

      const res = await fetch(
        `http://localhost:8080/api/proteticos/${usuario.id}/servicos/ativos`
      );

      const data = await res.json();
      setServicos(data);
    } catch (err) {
      console.error("Erro ao carregar serviços:", err);
    } finally {
      setCarregando(false);
    }
  };

  if (carregando)
    return <div className="p-8 text-xl">Carregando serviços...</div>;

  return (
    <div className="p-8 space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-800">Meus Serviços</h1>

        <button
          onClick={() => router.push("/protetico/perfil/servicos/novo")}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          ➕ Novo Serviço
        </button>
      </div>

      {/* LISTA DE SERVIÇOS */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {servicos.map((servico) => (
          <div
            key={servico.id}
            className="bg-white border p-4 rounded-xl shadow-sm hover:shadow-md"
          >
            <h2 className="text-xl font-bold">{servico.nomeServico}</h2>
            <p className="text-gray-600">{servico.descricao}</p>

            <p className="mt-2 text-lg font-semibold text-blue-600">
              R$ {servico.preco.toFixed(2)}
            </p>

            <p className="text-sm text-gray-600">
              Tempo médio: {servico.tempoMedioHoras}h
            </p>

            <div className="flex justify-end mt-4">
              <button
                onClick={() =>
                  router.push(
                    `/protetico/perfil/servicos/editar?id=${servico.id}`
                  )
                }
                className="text-blue-600 hover:text-blue-800 font-medium"
              >
                ✏️ Editar
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
