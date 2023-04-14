package br.com.phc.brasileiraoapi.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import br.com.phc.brasileiraoapi.util.dto.PartidaGoogleDTO;

public class ScrapingUtil {
	//Utilitário para a produção de logs
	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
	//URL do google, usado nas pesquisas.
	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	//Complemento da URL, para o caso, páginas do Brasil (pt-BR).
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	
	public static void main(String[] args) {
		String url = BASE_URL_GOOGLE + "santa+cruz+x+iguatu" + COMPLEMENTO_URL_GOOGLE;
		
		ScrapingUtil scraping = new ScrapingUtil();
		scraping.obtemInformacoesPartida(url);
	}
	//Método para obter informações da partida.
	public PartidaGoogleDTO obtemInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();
		
		//Documento Jsoup.
		Document document = null;
		
		//Conectando à página Google, a fim de fazer a pesquisa e o scraping(Raspagem) dos dados da página.
		try {
			document = Jsoup.connect(url).get();
			
			//Recuperar o título da página pesquisada.
			String title = document.title();
			LOGGER.info("Titulo da pagina: {}", title);
			
			StatusPartida statusPartida = obtemStatusPartida(document);
			LOGGER.info("Status da Partida: {}", statusPartida);
						
			if(statusPartida != StatusPartida.PARTIDA_NAO_INICIADA) {
				String tempoPartida = obtemTempoPartida(document);
				LOGGER.info("Tempo Partida: {}", tempoPartida);
			}
									
			//Pegando o nome do time mandante.
			String nomeEquipeCasa = recuperaNomeEquipeCasa(document);
			LOGGER.info("Nome da Equipe Mandante: {}", nomeEquipeCasa);
			
			//Pegando o nome do time visitante.
			String nomeEquipeVisitante = recuperaNomeEquipeVisitante(document);
			LOGGER.info("Nome da Equipe Visitante: {}", nomeEquipeVisitante);
			
			//Pegando o logotipo do time da casa
			String logoEquipeCasa = recuperaLogoEquipeCasa(document);
			LOGGER.info("Nome da Equipe Casa: {}", logoEquipeCasa);
			
			//Pegando o logotipo do time visitante
			String logoEquipeVisitante = recuperaLogoEquipeVisitante(document);
			LOGGER.info("Nome da Equipe Visitante: {}", logoEquipeVisitante);
			
		} catch(IOException e) {
			LOGGER.error("Erro ao tentar conectar no Google com Jsoup -> {}", e.getMessage());
		}
		
		return partida;
	}	
	
	//Método para conectar na página do Google(Document Jsoup) e obter o resultado da partida.
	public StatusPartida obtemStatusPartida(Document document) {
		StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;
		
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		if(!isTempoPartida) {
			String tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			
			if(tempoPartida.contains("Pênaltis")) {
				statusPartida = StatusPartida.PARTIDA_PENALTIS;
			}
			LOGGER.info(tempoPartida);
		} 
		
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		
		if(!isTempoPartida) {
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}
		
		LOGGER.info(statusPartida.toString());
		return statusPartida;		
	}
	
	//Método para pegar o tempo da partida.
	public String obtemTempoPartida(Document document) {
		String tempoPartida = null;
		
		//Jogo rolando, intervalo ou penalidades
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		
		if(!isTempoPartida) {
			tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
		}
		
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		
		if(!isTempoPartida) {
			tempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").first().text();
		}
		
		LOGGER.info(corrigeTempoPartida(tempoPartida));
		return tempoPartida;
	}
	
	//Método para melhorar o dado da partida.
	public String corrigeTempoPartida(String tempo) {
		String tempoPartida = null;
		
		if(tempo.contains("'")) {
			return tempo.replace("'", " min");
		} else if(tempo.contains("+")) {
			return tempo.replace(" ", "").concat(" min");
		} else {
			return tempo;
		}
	}
	
	public String recuperaNomeEquipeCasa(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-loa imso-ut imso-tnol]");
		String nomeEquipe = elemento.select("span").text();
		
		return nomeEquipe;
	}
	
	public String recuperaNomeEquipeVisitante(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-loa imso-ut imso-tnol]");
		String nomeEquipe = elemento.select("span").text();
		
		return nomeEquipe;
	}
	
	public String recuperaLogoEquipeCasa(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + elemento.select("img[class=imso_btl__mh_logo]").attr("src");
		
		return urlLogo;
	}
	
	public String recuperaLogoEquipeVisitante(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + elemento.select("img[class=imso_btl__mh_logo]").attr("src");
		
		return urlLogo;
	}
}
