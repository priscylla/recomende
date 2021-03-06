package br.recomende.model.recommender.engine.mine.curriculum;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.recomende.model.curriculum.BibliographicProduction;
import br.recomende.model.curriculum.CurriculumVitae;
import br.recomende.model.curriculum.Languages;
import br.recomende.model.entity.Profile;
import br.recomende.model.recommender.api.annotation.BeginMethod;
import br.recomende.model.recommender.api.annotation.Miner;
import br.recomende.model.recommender.engine.mine.curriculum.TermField;
import br.recomende.model.recommender.engine.mine.curriculum.TermScorer;

@Miner
public class CurriculumDataMiner {
	
	private TermScorer termScorer;
	private Logger log = LoggerFactory.getLogger(CurriculumDataMiner.class);
	
	@Autowired
	public CurriculumDataMiner(TermScorer termScorer) {
		this.termScorer = termScorer;
	}

	@BeginMethod
	public Profile suggest(CurriculumVitae curriculumVitae) {
		for (BibliographicProduction production : curriculumVitae.getBibliographicProductions()) {
			try {
				Languages titleLanguage = Languages.getType(production.getLanguage());
				if (titleLanguage == Languages.EN) {//Protótipo apenas com inglês
					termScorer.score(TermField.TITLE, titleLanguage, production.getTitle(), production.getRelevant());
					termScorer.score(TermField.KEYWORDS, titleLanguage, production.getKeywords(), production.getRelevant());
				}
			} catch (IOException e) {
				log.debug("Error on curriculum tag suggestion", e);
				continue;
			}
		}
		Map<String, Double> terms = termScorer.getScores();
		return new Profile(terms);
	}
	
}
