package simpledb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Table {
	
	private DbFile m_file;
	private String m_name;
	private String m_pkeyField;

	public Table(Dbfile file, String name, String pkeyField)
	{
		this.m_file = file;
		this.m_name = name;
		this.m_pkeyField = pkeyField;
	}

	public DbFile getFile()
		return this.m_file;

	public String getName()
		return this.m_name;

	public String getPkey()
		return this.m_pkeyField;

}
