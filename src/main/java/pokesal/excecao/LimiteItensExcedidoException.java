package pokesal.excecao;

/**
 * Lançada quando o treinador tenta usar mais itens do que o limite da batalha.
 */
public class LimiteItensExcedidoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Cria a exceção com a mensagem informada.
   *
   * @param mensagem detalhe do erro
   */
  public LimiteItensExcedidoException(String mensagem) {
    super(mensagem);
  }
}
