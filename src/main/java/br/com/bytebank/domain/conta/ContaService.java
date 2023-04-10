package br.com.bytebank.domain.conta;

import java.math.BigDecimal;
import java.util.Set;

import br.com.bytebank.domain.RegraDeNegocioException;

public class ContaService {

    public Set<Conta> listarContasAbertas() {
        return new ContaDAO().listar();
    }
    
    public Conta obterConta(Integer numeroDaConta) {
    	return buscarContaPorNumero(numeroDaConta);
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
    	new ContaDAO().salvar(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }
        
        if (!conta.getEstaAtiva()) {
            throw new RegraDeNegocioException("Conta não está ativa");
        }

        BigDecimal novoValor = conta.getSaldo().subtract(valor);
        new ContaDAO().alterarSaldo(novoValor, conta);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }
        
        if (!conta.getEstaAtiva()) {
            throw new RegraDeNegocioException("Conta não está ativa");
        }
        
        BigDecimal novoValor = conta.getSaldo().add(valor);
        new ContaDAO().alterarSaldo(novoValor, conta);
    }

    public void realizarTransferencia(Integer numeroDaContaOrigem, Integer numeroDaContaDestino, BigDecimal valor) {
    	realizarSaque(numeroDaContaOrigem, valor);
    	realizarDeposito(numeroDaContaDestino, valor);
    }
    
    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        new ContaDAO().deletar(numeroDaConta);
    }
    
    public void encerrarLogico(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        new ContaDAO().alterarLogico(numeroDaConta);
    }

    private Conta buscarContaPorNumero(Integer numero) {
        Conta conta = new ContaDAO().obter(numero);
    	
        if(conta == null) {
        	throw new RegraDeNegocioException("Conta não encontrada!");
        }
        
        return conta;
    }
}
