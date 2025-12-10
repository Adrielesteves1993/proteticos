'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'

export default function Login() {
  const [dados, setDados] = useState({
    email: '',
    senha: ''
  })
  const [carregando, setCarregando] = useState(false)
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setCarregando(true)

    try {
      const response = await fetch('http://localhost:8080/api/usuarios/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: dados.email,
          senha: dados.senha
        })
      })

      if (response.ok) {
        const usuario = await response.json()
        
        // Salva usuário no localStorage
        localStorage.setItem('usuario', JSON.stringify(usuario))
        
        // Redireciona para o dashboard correto baseado no tipo
        if (usuario.tipo === 'DENTISTA') {
          router.push('/dentista/dashboard')
        } else if (usuario.tipo === 'PROTETICO') {
          router.push('/protetico/dashboard')
        } else {
          router.push('/dashboard')
        }
        
      } else {
        const erro = await response.text()
        alert(erro || 'Credenciais inválidas')
      }
    } catch (error) {
      console.error('Erro no login:', error)
      alert('Erro ao conectar com o servidor')
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Área do Dentista - ProtéticoLab
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Ou{' '}
            <a href="#" className="font-medium text-indigo-600 hover:text-indigo-500">
              acesse com um convite
            </a>
          </p>
        </div>
        
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="rounded-md shadow-sm -space-y-px">
            <div>
              <label htmlFor="email-address" className="sr-only">
                Email
              </label>
              <input
                id="email-address"
                name="email"
                type="email"
                autoComplete="email"
                required
                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Endereço de email"
                value={dados.email}
                onChange={(e) => setDados({...dados, email: e.target.value})}
              />
            </div>
            <div>
              <label htmlFor="password" className="sr-only">
                Senha
              </label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Senha"
                value={dados.senha}
                onChange={(e) => setDados({...dados, senha: e.target.value})}
              />
            </div>
          </div>

          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <input
                id="remember-me"
                name="remember-me"
                type="checkbox"
                className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
              />
              <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">
                Lembrar de mim
              </label>
            </div>

            <div className="text-sm">
              <a href="#" className="font-medium text-indigo-600 hover:text-indigo-500">
                Esqueceu sua senha?
              </a>
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={carregando}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-200 disabled:opacity-50"
            >
              <span className="absolute left-0 inset-y-0 flex items-center pl-3">
                <svg
                  className="h-5 w-5 text-indigo-500 group-hover:text-indigo-400"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                  aria-hidden="true"
                >
                  <path
                    fillRule="evenodd"
                    d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                    clipRule="evenodd"
                  />
                </svg>
              </span>
              {carregando ? 'Entrando...' : 'Entrar'}
            </button>
          </div>
        </form>

        <div className="mt-6">
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-gray-300" />
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-2 bg-gray-50 text-gray-500">Não tem conta?</span>
            </div>
          </div>

          <div className="mt-6">
            <button
              onClick={() => router.push('/')}
              className="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
            >
              Voltar para Home
            </button>
          </div>
        </div>

        {/* Área de teste rápido */}
        <div className="mt-8 p-4 bg-gray-100 rounded-lg">
          <p className="text-sm text-gray-600 text-center mb-2">Teste rápido:</p>
          <div className="flex gap-2 justify-center">
            <button
              type="button"
              onClick={() => setDados({ email: 'joao@clinica.com', senha: '123456' })}
              className="text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded"
            >
              Dentista
            </button>
            <button
              type="button"
              onClick={() => setDados({ email: 'carlos@lab.com', senha: '123456' })}
              className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded"
            >
              Protético
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}