package mainPackage;

public class ListItems {
	public String name;
	boolean handCompatible = false, fingerCompatible = false;
	public ListItems()
	{
		name = "";
	}
	public ListItems(String n, boolean hC, boolean fC)
	{
		name = n;
		handCompatible = hC;
		fingerCompatible = fC;
	}
}
