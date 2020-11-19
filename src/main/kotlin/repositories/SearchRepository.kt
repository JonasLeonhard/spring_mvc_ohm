package repositories

import models.Search
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SearchRepository : JpaRepository<Search, String> {

    @Query("SELECT search FROM Search search ORDER BY search.updatedAt DESC")
    fun getRecentSearchTerms(pageable: Pageable): MutableList<Search>

    @Query("SELECT search FROM Search search ORDER BY search.searchAmount DESC")
    fun getMostPopularSearchTerms(pageable: Pageable): MutableList<Search>
}