package be.ugent.caagt.jmathtex;

public class TestDefaultTeXFont {
	public void test() {
		float size = 10.0f;
		DefaultTeXFont dtf = new DefaultTeXFont(size);
		
		System.out.print("dtf = " + dtf.toString());
	}
}
