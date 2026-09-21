package pokesal.batalha;

import pokesal.modelo.TipoElemental;

/**
 * Matriz de vantagens e desvantagens entre Fogo, Água e Planta.
 */
public class TabelaVantagem {

  /**
   * Devolve o multiplicador de dano do tipo atacante contra o defensor.
   *
   * @param atacante tipo de quem ataca
   * @param defensor tipo de quem recebe o golpe
   * @return 2.0, 0.5 ou 1.0
   */
  public double obterMultiplicador(TipoElemental atacante,
      TipoElemental defensor) {
    if (atacante == null || defensor == null) {
      return ConstantesBatalha.NEUTRO;
    }
    if (atacante == TipoElemental.FOGO && defensor == TipoElemental.PLANTA) {
      return ConstantesBatalha.SUPER_EFETIVO;
    }
    if (atacante == TipoElemental.FOGO && defensor == TipoElemental.AGUA) {
      return ConstantesBatalha.POUCO_EFETIVO;
    }
    if (atacante == TipoElemental.AGUA && defensor == TipoElemental.FOGO) {
      return ConstantesBatalha.SUPER_EFETIVO;
    }
    if (atacante == TipoElemental.AGUA && defensor == TipoElemental.PLANTA) {
      return ConstantesBatalha.POUCO_EFETIVO;
    }
    if (atacante == TipoElemental.PLANTA && defensor == TipoElemental.AGUA) {
      return ConstantesBatalha.SUPER_EFETIVO;
    }
    if (atacante == TipoElemental.PLANTA && defensor == TipoElemental.FOGO) {
      return ConstantesBatalha.POUCO_EFETIVO;
    }
    return ConstantesBatalha.NEUTRO;
  }
}
