package br.com.phc.brasileiraoapi.util.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaGoogleDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//Informações da partida.
	private String statusPartida;
	private String tempoPartida;
	
	//Informações da equipe da casa.
	private String nomeEquipeCasa;
	private String urlEquipeCasa;
	private Integer placarEquipeCasa;
	private String golsEquipeCasa;
	private String placarEstendidoEquipeCasa;
	
	//Informações da equipe da visitante.
	private String nomeEquipeVisitante;
	private String urlEquipeVisitante;
	private Integer placarEquipeVisitante;
	private String golsEquipeVisitante;
	private String placarEstendidoEquipeVisitante;	
}
