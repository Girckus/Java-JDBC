package br.com.bytebank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import br.com.bytebank.ConnectionFactory;
import br.com.bytebank.domain.cliente.Cliente;
import br.com.bytebank.domain.cliente.DadosCadastroCliente;

public class ContaDAO {

	private ConnectionFactory connectionFactory;

    public ContaDAO() {
        this.connectionFactory = ConnectionFactory.getInstance();
    }

    public void salvar(DadosAberturaConta dadosDaConta) {
        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connectionFactory.getConnection();
        	 PreparedStatement ps = conn.prepareStatement(sql)) {
            
        	ps.setInt(1, dadosDaConta.numero());
        	ps.setBigDecimal(2, BigDecimal.ZERO);
        	ps.setString(3, dadosDaConta.dadosCliente().nome());
        	ps.setString(4, dadosDaConta.dadosCliente().cpf());
        	ps.setString(5, dadosDaConta.dadosCliente().email());

        	ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
	
    public Set<Conta> listar() {
    	Set<Conta> contas = new HashSet<>();
    	
    	String sql = "SELECT * FROM conta WHERE esta_ativa = true";

        try (Connection conn = connectionFactory.getConnection();
        	 PreparedStatement ps = conn.prepareStatement(sql);
        	 ResultSet rs = ps.executeQuery()) {
            
        	while(rs.next()) {
        		Integer numero = rs.getInt(1);
        		BigDecimal saldo = rs.getBigDecimal(2);
        		String nome = rs.getString(3);
        		String cpf = rs.getString(4);
        		String email = rs.getString(5);
        		Boolean estaAtiva = rs.getBoolean(6);
        		
        		DadosCadastroCliente cliente = new DadosCadastroCliente(nome, cpf, email);
        		Conta conta = new Conta(numero, saldo, new Cliente(cliente), estaAtiva);
        		
        		contas.add(conta);
        	}
        	
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return contas;
    }

	public Conta obter(Integer numeroDaConta) {
		Conta conta = null;
    	
    	String sql = "SELECT * FROM conta WHERE esta_ativa = true AND numero = ?";

        try (Connection conn = connectionFactory.getConnection();
        	 PreparedStatement ps = conn.prepareStatement(sql)) {
        	 
        	ps.setInt(1, numeroDaConta);

        	try (ResultSet rs = ps.executeQuery()) {
	        	while(rs.next()) {
	        		Integer numero = rs.getInt(1);
	        		BigDecimal saldo = rs.getBigDecimal(2);
	        		String nome = rs.getString(3);
	        		String cpf = rs.getString(4);
	        		String email = rs.getString(5);
	        		Boolean estaAtiva = rs.getBoolean(6);
	        		
	        		DadosCadastroCliente cliente = new DadosCadastroCliente(nome, cpf, email);
	        		conta = new Conta(numero, saldo, new Cliente(cliente), estaAtiva);
	        	}
        	}
        	
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return conta;
	}
	
	public void alterarSaldo(BigDecimal saldo, Conta conta) {
    	String sql = "UPDATE conta set saldo = ? WHERE numero = ?";

        try (Connection conn = connectionFactory.getConnection();
        	 PreparedStatement ps = conn.prepareStatement(sql)) {
        	 
        	ps.setBigDecimal(1, saldo);
        	ps.setInt(2, conta.getNumero());

        	ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
	}
	
	public void deletar(Integer numeroDaConta) {
        String sql = "DELETE FROM conta WHERE numero = ?";

        try (Connection conn = connectionFactory.getConnection();
           	 PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroDaConta);

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void alterarLogico(Integer numeroDaConta) {
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ?";

        try (Connection conn = connectionFactory.getConnection();
           	 PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroDaConta);

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}