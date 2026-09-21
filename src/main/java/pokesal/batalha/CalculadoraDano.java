package pokesal.batalha;

import pokesal.modelo.Pokesal;
import pokesal.modelo.Terreno;
import pokesal.modelo.TipoElemental;
import pokesal.modelo.TipoGolpe;

/**
 * Calcula o dano de um golpe com vantagem elemental e efeito de terreno.
 */
public class CalculadoraDano {

  private final TabelaVantagem tabelaVantagem;

  /**
   * Cria a calculadora com a tabela de tipos padrão.
   */
  public CalculadoraDano() {
    this.tabelaVantagem = new TabelaVantagem();
  }

  /**
   * Calcula o dano de um golpe.
   *
   * <p>Fórmula: {@code (ATK * tipo * terreno * 10) / DEF}, arredondado, com
   * mínimo de 1.
   *
   * @param atacante quem ataca
   * @param defensor quem recebe
   * @param terreno terreno do estacionamento
   * @return dano inteiro maior ou igual a {@link ConstantesBatalha#DANO_MINIMO}
   */
  public int calcular(Pokesal atacante, Pokesal defensor, Terreno terreno) {
    return calcular(atacante, defensor, terreno, TipoGolpe.ELEMENTAL, false);
  }

  /**
   * Calcula o dano de um golpe, com combo e tipo de golpe.
   *
   * @param atacante quem ataca
   * @param defensor quem recebe
   * @param terreno terreno do estacionamento
   * @param golpe elemental (usa a tabela de tipos) ou Investida (neutro)
   * @param combo true se for o mesmo golpe do turno anterior
   * @return dano inteiro maior ou igual a {@link ConstantesBatalha#DANO_MINIMO}
   */
  public int calcular(Pokesal atacante, Pokesal defensor, Terreno terreno,
      TipoGolpe golpe, boolean combo) {
    if (atacante == null || defensor == null) {
      return ConstantesBatalha.DANO_MINIMO;
    }
    double tipoMult = ConstantesBatalha.NEUTRO;
    if (golpe == TipoGolpe.ELEMENTAL) {
      tipoMult = this.tabelaVantagem.obterMultiplicador(
          atacante.getTipo(), defensor.getTipo());
    }
    double terrenoMult = ConstantesBatalha.NEUTRO;
    if (golpe == TipoGolpe.ELEMENTAL) {
      terrenoMult = obterMultiplicadorTerreno(atacante.getTipo(), terreno);
    }
    int atk = atacante.getAtkEfetivo();
    int def = defensor.getDefEfetivo();
    double bruto = (atk * tipoMult * terrenoMult * ConstantesBatalha.FATOR_DANO)
        / (double) def;
    if (combo) {
      bruto = bruto * (1.0 + ConstantesBatalha.BONUS_COMBO);
    }
    int dano = (int) Math.round(bruto);
    if (dano < ConstantesBatalha.DANO_MINIMO) {
      return ConstantesBatalha.DANO_MINIMO;
    }
    return dano;
  }

  /**
   * Multiplicador extra do estacionamento.
   *
   * @param tipoAtacante tipo do golpe (mesmo tipo do Pokésal)
   * @param terreno terreno atual
   * @return 1.15 (fogo no asfalto), 1.10 (água na poça) ou 1.0
   */
  public double obterMultiplicadorTerreno(TipoElemental tipoAtacante,
      Terreno terreno) {
    if (terreno == Terreno.ASFALTO_QUENTE
        && tipoAtacante == TipoElemental.FOGO) {
      return 1.0 + ConstantesBatalha.BONUS_FOGO_ASFALTO;
    }
    if (terreno == Terreno.POCA_CHUVA && tipoAtacante == TipoElemental.AGUA) {
      return 1.0 + ConstantesBatalha.BONUS_AGUA_POCA;
    }
    return ConstantesBatalha.NEUTRO;
  }
}
