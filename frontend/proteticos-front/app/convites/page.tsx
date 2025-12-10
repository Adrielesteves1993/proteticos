// app/convites/page.tsx
"use client"

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'

interface Convite {
  id: number
  codigo: string
  tipo: string
  emailConvidado: string
  criadoEm: string
  expiraEm: string
  utilizado: boolean
}

export default function ConvitesPage() {
  const router = useRouter()
  const [convites, setConvites] = useState<Convite[]>([])
  const [carregando, setCarregando] = useState(true)

  useEffect(() => {
    carregarConvites()
  }, [])

  const carregarConvites = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/convites')
      if (response.ok) {
        const data = await response.json()
        setConvites(data)
      }
    } catch (error) {
      console.error('Erro ao carregar convites:', error)
    } finally {
      setCarregando(false)
    }
  }

  const getStatusBadge = (utilizado: boolean, expiraEm: string) => {
    if (utilizado) return <span className="bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs">✅ Utilizado</span>
    if (new Date(expiraEm) < new Date()) return <span className="bg-red-100 text-red-800 px-2 py-1 rounded-full text-xs">❌ Expirado</span>
    return <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs">⏳ Ativo</span>
  }

  const getTipoBadge = (tipo: string) => {
    return tipo === 'DENTISTA' 
      ? <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs">🦷 Dentista</span>
      : <span className="bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs">🔧 Protético</span>
  }

  return (
    <div className="min-h-screen bg-gray-50 p-4 md:p-8">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Convites</h1>
              <p className="text-gray-600 mt-2">Gerencie convites para novos usuários</p>
            </div>
            <button
              onClick={() => router.push('/convites/gerar')}
              className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-3 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 shadow-md"
            >
              + Novo Convite
            </button>
          </div>
        </div>

        {/* Estatísticas */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-xl p-6 shadow border border-gray-200">
            <p className="text-sm font-medium text-gray-600">Total de Convites</p>
            <p className="text-3xl font-bold text-gray-900">{convites.length}</p>
          </div>
          <div className="bg-white rounded-xl p-6 shadow border border-gray-200">
            <p className="text-sm font-medium text-gray-600">Ativos</p>
            <p className="text-3xl font-bold text-blue-600">
              {convites.filter(c => !c.utilizado && new Date(c.expiraEm) >= new Date()).length}
            </p>
          </div>
          <div className="bg-white rounded-xl p-6 shadow border border-gray-200">
            <p className="text-sm font-medium text-gray-600">Utilizados</p>
            <p className="text-3xl font-bold text-green-600">
              {convites.filter(c => c.utilizado).length}
            </p>
          </div>
          <div className="bg-white rounded-xl p-6 shadow border border-gray-200">
            <p className="text-sm font-medium text-gray-600">Expirados</p>
            <p className="text-3xl font-bold text-red-600">
              {convites.filter(c => !c.utilizado && new Date(c.expiraEm) < new Date()).length}
            </p>
          </div>
        </div>

        {/* Tabela de Convites */}
        <div className="bg-white rounded-xl shadow border border-gray-200 overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900">Lista de Convites</h2>
          </div>
          
          {carregando ? (
            <div className="p-12 text-center">
              <div className="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
              <p className="mt-4 text-gray-600">Carregando convites...</p>
            </div>
          ) : convites.length === 0 ? (
            <div className="p-12 text-center">
              <div className="text-gray-400 text-5xl mb-4">📭</div>
              <p className="text-gray-600">Nenhum convite encontrado</p>
              <button
                onClick={() => router.push('/convites/gerar')}
                className="mt-4 text-blue-600 hover:text-blue-800 font-medium"
              >
                Gerar primeiro convite →
              </button>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Código</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tipo</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Criado em</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Expira em</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ações</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {convites.map((convite) => (
                    <tr key={convite.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <code className="font-mono text-sm bg-gray-100 px-2 py-1 rounded">
                          {convite.codigo}
                        </code>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {getTipoBadge(convite.tipo)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {convite.emailConvidado || '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {new Date(convite.criadoEm).toLocaleDateString('pt-BR')}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {new Date(convite.expiraEm).toLocaleDateString('pt-BR')}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {getStatusBadge(convite.utilizado, convite.expiraEm)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button
                          onClick={() => navigator.clipboard.writeText(convite.codigo)}
                          className="text-blue-600 hover:text-blue-900 mr-3"
                        >
                          📋 Copiar
                        </button>
                        {!convite.utilizado && (
                          <button className="text-red-600 hover:text-red-900">
                            ❌ Excluir
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}