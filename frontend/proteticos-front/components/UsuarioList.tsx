'use client';

import { useEffect, useState } from 'react';
import { Usuario } from '@/types/usuario';
import { apiGet } from '@/lib/api';

export default function UsuarioList() {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadUsuarios() {
      try {
        const data = await apiGet('/usuarios');
        setUsuarios(data);
      } catch (err) {
        setError('Erro ao carregar usuários');
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    }
    loadUsuarios();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center p-8">
        <div className="text-lg">Carregando usuários...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
        {error}
      </div>
    );
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Usuários Cadastrados</h1>
      
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {usuarios.map(usuario => (
          <div 
            key={usuario.id} 
            className="border border-gray-200 rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow"
          >
            <div className="flex items-start justify-between mb-3">
              <h3 className="font-semibold text-lg text-gray-800">{usuario.nome}</h3>
              <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                usuario.tipo === 'DENTISTA' 
                  ? 'bg-blue-100 text-blue-800' 
                  : 'bg-green-100 text-green-800'
              }`}>
                {usuario.tipo}
              </span>
            </div>
            
            <p className="text-gray-600 mb-2">📧 {usuario.email}</p>
            
            <div className="flex items-center justify-between mt-4">
              <span className={`px-2 py-1 rounded text-xs ${
                usuario.ativo 
                  ? 'bg-green-100 text-green-800' 
                  : 'bg-red-100 text-red-800'
              }`}>
                {usuario.ativo ? '✅ Ativo' : '❌ Inativo'}
              </span>
              
              <span className="text-xs text-gray-500">
                {new Date(usuario.dataCriacao).toLocaleDateString('pt-BR')}
              </span>
            </div>
          </div>
        ))}
      </div>

      {usuarios.length === 0 && (
        <div className="text-center py-8 text-gray-500">
          Nenhum usuário cadastrado
        </div>
      )}
    </div>
  );
}