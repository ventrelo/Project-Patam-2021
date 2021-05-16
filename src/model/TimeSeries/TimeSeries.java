package model.TimeSeries;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TimeSeries {

	private List<String> headers = new ArrayList<String>();
	private List<Float[]> values = new ArrayList<Float[]>();
	public TimeSeries(String csvFileName)  {

		BufferedReader reader = null;
		Scanner scanner = null;
		int pos;
		String str = null;

		try {
			reader = new BufferedReader(new FileReader(csvFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			if((str = reader.readLine()) != null)
			{
				scanner = new Scanner(str);
				scanner.useDelimiter(",");
				while(scanner.hasNext())
					headers.add(scanner.next());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		int numOfElements = headers.size();
		while(true) {
			try {
				if ((str = reader.readLine()) == null) break;
			} catch (IOException e) {
				e.printStackTrace();
			}

			pos = 0;
			Float[] f = new Float[numOfElements];
			scanner = new Scanner(str);
			scanner.useDelimiter(",");
			while(scanner.hasNextFloat())
				{
					f[pos] = scanner.nextFloat();
					pos++;
				}
			values.add(f);
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
	}

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public List<Float[]> getValues() {
		return values;
	}

	public void setValues(List<Float[]> values) {
		this.values = values;
	}
}
