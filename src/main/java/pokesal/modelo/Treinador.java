package pokesal.modelo;

import pokesal.batalha.ConstantesBatalha;
import pokesal.excecao.LimiteItensExcedidoException;

/**
 * Treinador do torneio, com um único Pokésal inicial e controle da mochila.
 */
public class Treinador {

  private final String nome;
  private final Pokesal pokesal;
  private int itensUsados;

  /**
   * Cria o treinador já com o inicial escolhido.
   *
   * @param nome nome do treinador
   * @param pokesal inicial da batalha
   */
  public Treinador(String nome, Pokesal pokesal) {
    if (nome == null || nome.trim().isEmpty()) {
      throw new IllegalArgumentException("Nome do treinador e obrigatorio.");
    }
    if (pokesal == null) {
      throw new IllegalArgumentException("O treinador precisa de um Pokesal.");
    }
    this.nome = nome;
    this.pokesal = pokesal;
    this.itensUsados = 0;
  }

  /**
   * Usa um item da mochila. Consome uma das duas usos permitidos por batalha.
   *
   * @param item item escolhido
   * @throws LimiteItensExcedidoException se o limite de 2 itens já foi atingido
   */
  public void usarItem(TipoItem item) {
    if (item == null) {
      throw new IllegalArgumentException("Item e obrigatorio.");
    }
    if (this.itensUsados >= ConstantesBatalha.LIMITE_ITENS_POR_BATALHA) {
      throw new LimiteItensExcedidoException(
          this.nome + " ja usou o limite de "
              + ConstantesBatalha.LIMITE_ITENS_POR_BATALHA
              + " itens nesta batalha.");
    }
    this.itensUsados++;
    this.pokesal.resetarCombo();
    aplicarEfeito(item);
  }

  private void aplicarEfeito(TipoItem item) {
    if (item == TipoItem.POTION) {
      this.pokesal.curar(ConstantesBatalha.CURA_POTION);
    } else if (item == TipoItem.SUPER_POTION) {
      this.pokesal.curar(ConstantesBatalha.CURA_SUPER_POTION);
    } else if (item == TipoItem.ANTIDOTE) {
      this.pokesal.limparStatus();
    }
  }

  /**
   * @return nome do treinador
   */
  public String getNome() {
    return this.nome;
  }

  /**
   * @return Pokésal do treinador
   */
  public Pokesal getPokesal() {
    return this.pokesal;
  }

  /**
   * @return quantos itens já foram usados nesta batalha
   */
  public int getItensUsados() {
    return this.itensUsados;
  }

  /**
   * @return quantos itens ainda podem ser usados
   */
  public int getItensRestantes() {
    return ConstantesBatalha.LIMITE_ITENS_POR_BATALHA - this.itensUsados;
  }
}
