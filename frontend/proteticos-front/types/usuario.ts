export interface Usuario {
  id: number;
  nome: string;
  email: string;
  senha: string;
  tipo: 'DENTISTA' | 'PROTETICO';
  ativo: boolean;
  dataCriacao: string;
}

export interface Dentista extends Usuario {
  cro: string;
  especialidade: string;
  telefone: string;
  enderecoClinica: string | null;
}

export interface Protetico extends Usuario {
  registroProfissional: string;
  especializacao: string;
  aceitaTerceirizacao: boolean;
  valorHora: number | null;
  capacidadePedidosSimultaneos: number;
}