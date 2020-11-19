package services

import models.Search
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import repositories.SearchRepository

@Service
class SearchService(val searchRepository: SearchRepository) {
    fun addSearch(query: String, resultsFound: Int) {
        if (resultsFound > 0) {
            val searchOptional = searchRepository.findById(query)

            if (searchOptional.isPresent) {
                val search = searchOptional.get()
                search.searchAmount += 1
                searchRepository.save(search)
            } else {
                searchRepository.save(Search(query))
            }
        }
    }

    fun getMostPopularSearchTerms(amount: Int): MutableList<Search> {
        return searchRepository.getMostPopularSearchTerms(PageRequest.of(0, amount))
    }

    fun getRecentSearchTerms(amount: Int): MutableList<Search> {
        return searchRepository.getRecentSearchTerms(PageRequest.of(0, amount))
    }
}