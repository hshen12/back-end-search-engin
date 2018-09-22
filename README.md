# back-end-search-engine

This project is a memory based search engine in Java language. The goal for this search engine is to fetch the given URL and the URL on the page and do a search using multi threading technics.

The project will first grab the provided web page and clean simple, validating html 4/5 into plain-text words using Apache open-nlp library and store each words into a data structure.

It will output the data structure with in JSON format and perform a partial or exact search based on the query word provided.

After search, it will output the search result also in JSON format.
