import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import JSonHandling.ReadJSonFile.JSonObjectProcessor;




public class ProductContainer implements JSonObjectProcessor{

	protected Map<String, ProductFamily> manufFamilysMap = new HashMap<String, ProductFamily>();  
	protected UnmatchedStatistics unmathcedStatisObject;
	
	public void addProduct(Product product) {
		String manufacturer = product.getManufacturer().toLowerCase();
		ProductFamily productFamily = manufFamilysMap.get(manufacturer);
		if(productFamily == null) {
			productFamily = new ProductFamily();
			manufFamilysMap.put(manufacturer, productFamily);
		}
		productFamily.addProduct(product);
		
		if(unmathcedStatisObject != null )
			unmathcedStatisObject.addProduct(product);
	}
	
	public Product getMatchedProduct(Price price) {
		String manufacturer = price.getManufacturer().toLowerCase();
		ProductFamily productFamily = manufFamilysMap.get(manufacturer);
		if(productFamily == null) {
			manufacturer = manufacturer.split("\\s")[0];
			productFamily = manufFamilysMap.get(manufacturer);
			if(productFamily == null)
				return null;
		}
		return productFamily.getMatchedProduct(price);
		
	}
	
	protected static class ProductFamily {
		protected List<Product> noFamilyProductList = new ArrayList<Product>(); 
		protected Map<String, List<Product>>  familyProductsMap = new HashMap<String, List<Product>>(); 
		
		public void addProduct(Product product) {
			String family = product.getFamily();
			if(StringUtils.isEmpty(family)) {
				noFamilyProductList.add(product);
			}else {
				List<Product> productList = familyProductsMap.get(family);
				if(productList == null) {
					productList = new ArrayList<Product>();
					familyProductsMap.put(family, productList);
				}
				productList.add(product);
			}
		}
		
		public Product getMatchedProduct(Price price) {
			Product product = null;
			String title = price.getTitle().toLowerCase();
			for(Entry<String, List<Product>> entry : familyProductsMap.entrySet()) {
				String family = entry.getKey().toLowerCase();
				if(title.contains(family)) {
					List<Product> productList = entry.getValue();
					product = searchProduct(title, productList);
					if(product != null)
						break;
				}
			}
			if(product == null) {
				product = searchProduct(title, noFamilyProductList);
			}
			
			return product;
		}

		private Product searchProduct(String title, List<Product> productList) {
			for(Product product : productList) {
				if(matchModel(title, product))
					return product;
				else if(matchProductname(title, product))
					return product;
					
			}
			return null;
		}

		private boolean matchModel(String title, Product product) {
			String model = product.getModel().toLowerCase();
			if(title.matches(".*\\b" + model + "\\b.*"))
				return true;
			else 
				return SimiliarStringSearch.match(title, model);
		}
		
		private boolean matchProductname(String title, Product product) {
			String name = product.getProduct_name().toLowerCase();
			if(title.matches(".*\\b" + name + "\\b.*"))
				return true;
			else 
				return SimiliarStringSearch.match(title, name);
		}
	}

	
	
	public void processJSonObject(Object jsonObject) {
		Product product = (Product)jsonObject;
		addProduct(product);
		
	}

	public UnmatchedStatistics getUnmathcedStatisObject() {
		return unmathcedStatisObject;
	}

	public void setUnmathcedStatisObject(UnmatchedStatistics unmathcedStatisObject) {
		this.unmathcedStatisObject = unmathcedStatisObject;
	}
	
	
}
