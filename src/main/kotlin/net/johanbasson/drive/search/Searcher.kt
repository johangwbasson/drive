package net.johanbasson.drive.search

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.left
import arrow.core.right
import com.github.michaelbull.logging.InlineLogger
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.Environment
import net.johanbasson.drive.user.Principal
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.highlight.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.nio.file.Paths

data class SearchResult(
    val id: String,
    val title: String,
    val fragments: List<String>,
    val contentType: String,
    val size: String,
    val score: Float,
    val folder: String
)

fun search(
    env: Environment,
    principal: Principal,
    queryStr: String,
    maxResults: Int = 10
): Either<ApiError, List<SearchResult>>  {
    return try {
        val dir: Directory = FSDirectory.open(Paths.get(env.config.indexStoreConfig.path))
        val analyzer = StandardAnalyzer()
        val reader = DirectoryReader.open(dir)
        val searcher = IndexSearcher(reader)

        val query = QueryParser("content", analyzer).parse(queryStr)
        val hits = searcher.search(query, 10)
        val formatter = SimpleHTMLFormatter()
        val scorer = QueryScorer(query)
        val highlighter = Highlighter(formatter, scorer)
        val fragmenter = SimpleSpanFragmenter(scorer, 10)
        highlighter.textFragmenter = fragmenter

        val results = ArrayList<SearchResult>()

        hits.scoreDocs.forEach { d ->
            val doc = searcher.doc(d.doc)
            val content = doc.get("content")
            val title = doc.get("title")
            val size = doc.get("size")
            val contentType = doc.get("content-type")
            val folder = doc.get("folder")
            val noteId = doc.get("id")
            val userVal = doc.get("principal")

            if (userVal == principal.userId.toString()) {
                val stream = TokenSources.getAnyTokenStream(reader, d.doc, "content", analyzer)
                val frags = highlighter.getBestFragments(stream, content, maxResults)
                results.add(SearchResult(noteId, title, frags.toList(), contentType, size, d.score, folder))
            }

        }

        results.right()
    } catch (ex: Exception) {
        InlineLogger().error(ex) { "Unable to search" }
        ApiError.SearchError(ex.localizedMessage).left()
    }
}
