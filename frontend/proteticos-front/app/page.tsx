"use client"

import Link from 'next/link'
import { useState } from 'react'

export default function Home() {
  const [codigoConvite, setCodigoConvite] = useState('')
  const [mostrarAcessoConvite, setMostrarAcessoConvite] = useState(false)

  const handleAcessoPorConvite = (e: React.FormEvent) => {
    e.preventDefault()
    if (codigoConvite) {
      // Redireciona para a página de cadastro com o código
      window.location.href = `/cadastro/${codigoConvite}`
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-cyan-100">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div className="flex items-center">
              <div className="w-8 h-8 bg-cyan-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-sm">PL</span>
              </div>
              <span className="ml-2 text-xl font-bold text-gray-900">ProtéticoLab</span>
            </div>
            <nav className="hidden md:flex space-x-8">
              <a href="#" className="text-gray-600 hover:text-gray-900">Início</a>
              <a href="#" className="text-gray-600 hover:text-gray-900">Serviços</a>
              <a href="#" className="text-gray-600 hover:text-gray-900">Sobre</a>
              <a href="#" className="text-gray-600 hover:text-gray-900">Contato</a>
            </nav>
            <div className="flex gap-4">
              <button 
                onClick={() => setMostrarAcessoConvite(true)}
                className="bg-gray-600 text-white px-4 py-2 rounded-lg hover:bg-gray-700 transition duration-200"
              >
                Tenho um Convite
              </button>
              <Link 
                href="/login"
                className="bg-cyan-600 text-white px-6 py-2 rounded-lg hover:bg-cyan-700 transition duration-200"
              >
                Área do Dentista
              </Link>
            </div>
          </div>
        </div>
      </header>

      {/* Modal de Acesso por Convite */}
      {mostrarAcessoConvite && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">Acesso por Convite</h3>
              <button 
                onClick={() => setMostrarAcessoConvite(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                ✕
              </button>
            </div>
            <p className="text-gray-600 mb-4">
              Digite o código do convite que você recebeu:
            </p>
            <form onSubmit={handleAcessoPorConvite}>
              <input
                type="text"
                value={codigoConvite}
                onChange={(e) => setCodigoConvite(e.target.value.toUpperCase())}
                placeholder="Ex: PTLAB-DENT-ABC123"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg mb-4 text-center font-mono"
                required
              />
              <button
                type="submit"
                className="w-full bg-cyan-600 text-white py-2 px-4 rounded-lg hover:bg-cyan-700"
              >
                Acessar com Convite
              </button>
            </form>
            <p className="text-sm text-gray-500 mt-4 text-center">
              Não tem convite? Peça a um protético parceiro.
            </p>
          </div>
        </div>
      )}

      {/* Hero Section */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="text-center">
          <h1 className="text-4xl md:text-6xl font-bold text-gray-900 mb-6">
            <span className="block text-cyan-600">ProtéticoLab</span>
          </h1>
          
          <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
            Excelência em próteses dentárias com tecnologia de ponta e artesanato preciso. 
            Seu parceiro confiável para sorrisos perfeitos.
          </p>

          <div className="flex flex-col sm:flex-row gap-4 justify-center items-center mb-16">
            <Link 
              href="/login"
              className="bg-cyan-600 text-white px-8 py-4 rounded-lg text-lg font-semibold hover:bg-cyan-700 transition duration-200 shadow-lg"
            >
              Acessar Sistema
            </Link>
            <button 
              onClick={() => setMostrarAcessoConvite(true)}
              className="border-2 border-cyan-600 text-cyan-600 px-8 py-4 rounded-lg text-lg font-semibold hover:bg-cyan-50 transition duration-200"
            >
              Acessar com Convite
            </button>
          </div>

          {/* Features Grid */}
          <div className="grid md:grid-cols-3 gap-8 mt-16">
            <div className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition duration-200">
              <div className="w-12 h-12 bg-cyan-100 rounded-lg flex items-center justify-center mb-4">
                <svg className="w-6 h-6 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Qualidade Garantida</h3>
              <p className="text-gray-600">Materiais certificados e técnicas comprovadas para resultados duradouros.</p>
            </div>

            <div className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition duration-200">
              <div className="w-12 h-12 bg-cyan-100 rounded-lg flex items-center justify-center mb-4">
                <svg className="w-6 h-6 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Entrega Rápida</h3>
              <p className="text-gray-600">Prazos otimizados sem abrir mão da qualidade do trabalho artesanal.</p>
            </div>

            <div className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition duration-200">
              <div className="w-12 h-12 bg-cyan-100 rounded-lg flex items-center justify-center mb-4">
                <svg className="w-6 h-6 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Atendimento Personalizado</h3>
              <p className="text-gray-600">Dedicamos atenção especial a cada caso e necessidade específica.</p>
            </div>
          </div>

          {/* Nova Seção: Como Funciona */}
          <div className="mt-20">
            <h2 className="text-3xl font-bold text-gray-900 mb-8">Como Funciona</h2>
            <div className="grid md:grid-cols-4 gap-6">
              <div className="text-center">
                <div className="w-12 h-12 bg-cyan-600 text-white rounded-full flex items-center justify-center mx-auto mb-3">
                  1
                </div>
                <p className="font-semibold">Receba o Convite</p>
                <p className="text-sm text-gray-600">De um protético parceiro</p>
              </div>
              <div className="text-center">
                <div className="w-12 h-12 bg-cyan-600 text-white rounded-full flex items-center justify-center mx-auto mb-3">
                  2
                </div>
                <p className="font-semibold">Acesse com o Código</p>
                <p className="text-sm text-gray-600">Use o código recebido</p>
              </div>
              <div className="text-center">
                <div className="w-12 h-12 bg-cyan-600 text-white rounded-full flex items-center justify-center mx-auto mb-3">
                  3
                </div>
                <p className="font-semibold">Faça seu Cadastro</p>
                <p className="text-sm text-gray-600">Complete seu perfil</p>
              </div>
              <div className="text-center">
                <div className="w-12 h-12 bg-cyan-600 text-white rounded-full flex items-center justify-center mx-auto mb-3">
                  4
                </div>
                <p className="font-semibold">Comece a Usar</p>
                <p className="text-sm text-gray-600">Acesse todas as funcionalidades</p>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 mt-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex flex-col md:flex-row justify-between items-center">
            <div className="flex items-center mb-4 md:mb-0">
              <div className="w-6 h-6 bg-cyan-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-xs">PL</span>
              </div>
              <span className="ml-2 text-lg font-semibold text-gray-900">ProtéticoLab</span>
            </div>
            <div className="text-gray-600">
              &copy; {new Date().getFullYear()} ProtéticoLab. Todos os direitos reservados.
            </div>
          </div>
        </div>
      </footer>
    </div>
  )
}