package library;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Book book = new Book();
		String title = "";
		String author = "";
		String publisher = "";
		String publishDate = "";
		int price = 0;
		
		book.setTitle("실락원");
		book.setAuthor("Jhon Milton");
		book.setPublisher("황금가지");
		book.setPublishDate("2019.02.21");
		book.setPrice(12000);
		
		title = book.getTitle();
		author = book.getAuthor();
		publisher = book.getPublisher();
		publishDate = book.getPublishDate();
		price = book.getPrice();
		
		System.out.println(title);
		System.out.println(author);
		System.out.println(publisher);
		System.out.println(publishDate);
		System.out.println(price);
		
		
	}

}
