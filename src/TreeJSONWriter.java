import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeJSONWriter {
	
	private static DecimalFormat FORMATTER = new DecimalFormat("0.000000");

	/**
	 * Writes several tab <code>\t</code> symbols using the provided
	 * {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path)
			throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers
	 * using the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer,
			int level) throws IOException {

		writer.write("[");
		writer.write(System.lineSeparator());
		if(!elements.isEmpty()) {

			var first = elements.first();
			indent(level+1, writer);
			writer.write(first.toString());
			var next = elements.higher(first);

			while(next != null) {
				writer.write(",");
				writer.write(System.lineSeparator());

				indent(level+1, writer);
				writer.write(next.toString());
				next = elements.higher(next);

			}
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write("]");


	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path)
			throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer,
			int level) throws IOException {

		writer.write("{");
		writer.write(System.lineSeparator());
		indent(level, writer);
		if(!elements.isEmpty()) {

			var firstKey = elements.firstKey();
			indent(level+1, writer);
			writer.write("\"" + firstKey + "\": " + elements.get(firstKey).toString());
			var nextKey = elements.higherKey(firstKey);

			while(nextKey != null) {
				writer.write(",");
				writer.write(System.lineSeparator());

				indent(level+1, writer);
				writer.write("\"" + nextKey + "\": " + elements.get(nextKey).toString());

				nextKey = elements.higherKey(nextKey);
			}

			writer.write(System.lineSeparator());
		}
		writer.write("}");
	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Writer writer, int level) throws IOException {

		writer.write("{");
		writer.write(System.lineSeparator());

		if(!elements.isEmpty()) {
			var firstKey = elements.firstKey();

			indent(level+1, writer);
			writer.write("\""+ firstKey + "\": ");
			asArray(elements.get(firstKey), writer, level+1);

			var nextKey = elements.higherKey(firstKey);
			while(nextKey != null) {


				writer.write(",");
				writer.write(System.lineSeparator());
				indent(level+1, writer);
				writer.write("\"" + nextKey + "\": ");
				asArray(elements.get(nextKey), writer, level+1);
				nextKey = elements.higherKey(nextKey);
			}

			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write("}");
	}

	/**
	 * Returns the inverted index of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the inverted index to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asDoubleNestedObject(TreeMap, Writer, int)
	 */
	public static String asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asDoubleNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the inverted index of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param elements the inverted index to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asDoubleNestedObject(TreeMap, Writer, int)
	 */
	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asDoubleNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Write the inverted index data structure out using JSON format
	 * @param elements data structure 
	 * 	TreeMap<word, TreeMap<path, TreeSet<position>>>
	 * @param writer bufferedWriter to write to the file
	 * @param level indentation level, start from 0
	 * @throws IOException
	 * 
	 * @see {@link #asNestedObject(TreeMap, Writer, int)}
	 * @see #indent(int, Writer)
	 */
	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, 
			Writer writer, int level) throws IOException {

		indent(level, writer);
		writer.write("{");
		writer.write(System.lineSeparator());

		if(!elements.isEmpty()) {
			String firstKey = elements.firstKey();
			indent(level+1, writer);
			writer.write("\"" + firstKey + "\": ");
			asNestedObject(elements.get(firstKey), writer, level+1);
			String nextKey = elements.higherKey(firstKey);
			while(nextKey != null) {
				writer.write(",");
				writer.write(System.lineSeparator());
				indent(level+1, writer);
				writer.write("\"" + nextKey + "\": ");
				asNestedObject(elements.get(nextKey), writer, level+1);
				nextKey = elements.higherKey(nextKey);
			}
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write("}");
	}

	/**
	 * Writes the search result of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param elements the inverted index to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asSearchResult(TreeMap, Writer, int)
	 */
	public static void asSearchResult(TreeMap<TreeSet<String>, ArrayList<OneResult>> searchResult, 
			Path resultPath) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(resultPath,
				StandardCharsets.UTF_8)) {
			asSearchResult(searchResult, writer, 0);
		}
	}

	/**
	 * Returns the search result of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the inverted index to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asSearchResult(TreeMap, Writer, int)
	 */
	public static String asSearchResult(TreeMap<TreeSet<String>, ArrayList<OneResult>> searchResult) {
		try {
			StringWriter writer = new StringWriter();
			asSearchResult(searchResult, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Write the query word and the corresponding search result out using JSON format
	 * @param elements data structure 
	 * @param writer bufferedWriter to write to the file
	 * @param level indentation level, start from 0
	 * @throws IOException
	 * 
	 * @see #asQuery(TreeSet ,ArrayList, Writer, int)
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asSearchResult(TreeMap<TreeSet<String>, ArrayList<OneResult>> searchResult, Writer writer,
			int level) throws IOException {
		indent(level, writer);
		writer.write("[");
		writer.write(System.lineSeparator());
		if(!searchResult.isEmpty()) {
			Iterator<TreeSet<String>> iterator = searchResult.keySet().iterator();
			TreeSet<String> current = iterator.next();
			asQuery(current, searchResult.get(current), writer, level+1);
			while(iterator.hasNext()) {
				current = iterator.next();
				
				writer.write(",");
				writer.write(System.lineSeparator());
				asQuery(current, searchResult.get(current), writer, level+1);
			}
		}
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("]");
	}

	/**
	 * Write the query word and the corresponding search result out using JSON format
	 * @param elements data structure 
	 * @param writer bufferedWriter to write to the file
	 * @param level indentation level, start from 0
	 * @throws IOException
	 * 
	 * @see #asArrayList(ArrayList, Writer, int)
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	private static void asQuery(TreeSet<String> query, ArrayList<OneResult> list, Writer writer, 
			int level) throws IOException {
		indent(level, writer);
		writer.write("{");
		writer.write(System.lineSeparator());
		indent(level+1, writer);
		quote("queries", writer);
		writer.write(": ");
		quote(String.join(" ", query), writer);
		writer.write(",");
		writer.write(System.lineSeparator());
		indent(level+1, writer);
		quote("results", writer);
		writer.write(": ");
		writer.write("[");
		asArrayList(list, writer, level+1);
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("}");
	}

	/**
	 * Write the array list of search result out using JSON format
	 * @param elements array list of search result
	 * @param writer bufferedWriter to write to the file
	 * @param level indentation level, start from 0
	 * @throws IOException
	 * 
	 * @see {@link #asOneSearchResult(OneResult, Writer, int)}
	 */
	private static void asArrayList(ArrayList<OneResult> elements, Writer writer, int level) throws IOException{
		if(!elements.isEmpty()) {
			writer.write(System.lineSeparator());
			var iterator = elements.iterator();
			asOneSearchResult(iterator.next(), writer, level+1);
			while(iterator.hasNext()) {
				writer.write(",");
				writer.write(System.lineSeparator());
				asOneSearchResult(iterator.next(), writer, level+1);
			}
		} 
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("]");
	}

	/**
	 * Writer one search result out using JSON format
	 * @param elements one search result
	 * @param writer bufferedWriter to write to the file
	 * @param level indentation level
	 * @throws IOException
	 * 
	 * @see OneResult
	 */
	private static void asOneSearchResult(OneResult elements, Writer writer, int level) throws IOException {
		indent(level, writer);
		writer.write("{");
		if(elements != null) {
			writer.write(System.lineSeparator());
			indent(level+1, writer);
			quote("where", writer);
			writer.write(": ");
			quote(elements.getPath(), writer);
			writer.write(",");
			writer.write(System.lineSeparator());
			indent(level+1, writer);
			quote("count", writer);
			writer.write(": ");
			writer.write(String.valueOf(elements.getCount()));
			writer.write(",");
			writer.write(System.lineSeparator());
			indent(level+1, writer);
			quote("score" ,writer);
			writer.write(": ");
			writer.write(FORMATTER.format(elements.getScore()));
		}
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("}");
	}
}
