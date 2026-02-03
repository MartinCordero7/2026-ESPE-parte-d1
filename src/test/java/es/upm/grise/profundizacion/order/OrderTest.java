package es.upm.grise.profundizacion.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.upm.grise.exceptions.IncorrectItemException;

/**
 * Suite completa de pruebas para la clase Order.
 * 
 * El sistema Order representa un carrito de compras que gestiona items (productos).
 * Cada item contiene un producto, cantidad y precio.
 * 
 * Casos de prueba cubiertos:
 * 1. Validación de precios negativos (lanza excepción)
 * 2. Validación de cantidad cero o negativa (lanza excepción)
 * 3. Agregar items válidos a una orden vacía
 * 4. Fusión automática de items con el mismo producto y precio
 * 5. No fusión de items con el mismo producto pero diferente precio
 */
public class OrderTest {
	
	private Order order;
	private MockItem mockItem;
	private Product product1;
	private Product product2;
	
	/**
	 * Método setUp: Se ejecuta antes de cada prueba.
	 * Inicializa una nueva Order y crea objetos mock para las pruebas.
	 */
	@BeforeEach
	public void setUp() {
		order = new Order();
		
		// Creamos dos productos diferentes para usar en las pruebas
		product1 = new Product();
		product1.setId(1L);
		
		product2 = new Product();
		product2.setId(2L);
	}
	
	/**
	 * PRUEBA 1: Validación de precio negativo
	 * 
	 * Descripción: Cuando se intenta agregar un item con precio negativo,
	 * el método addItem() debe lanzar una IncorrectItemException.
	 * 
	 * Entrada: Item con precio = -10.0, cantidad = 1, producto válido
	 * Resultado esperado: Se lanza IncorrectItemException
	 * Verificación: La orden sigue vacía (sin cambios)
	 */
	@Test
	@DisplayName("Rechazo de item con precio negativo")
	public void testAddItemWithNegativePriceThrowsException() throws IncorrectItemException {
		// ARRANGE (Preparación)
		mockItem = new MockItem(product1, 1, -10.0);
		
		// ACT & ASSERT (Acción y Verificación)
		// assertThrows verifica que se lance la excepción al ejecutar addItem
		assertThrows(IncorrectItemException.class, () -> {
			order.addItem(mockItem);
		});
		
		// Verificamos que la orden continúa vacía (el item no fue agregado)
		assertEquals(0, order.getItems().size(), 
			"La orden debe permanecer vacía cuando se rechaza un item");
	}
	
	/**
	 * PRUEBA 2: Validación de cantidad cero
	 * 
	 * Descripción: Un item con cantidad cero es inválido.
	 * El método debe rechazarlo lanzando IncorrectItemException.
	 * 
	 * Entrada: Item con precio = 10.0, cantidad = 0
	 * Resultado esperado: Se lanza IncorrectItemException
	 */
	@Test
	@DisplayName("Rechazo de item con cantidad cero")
	public void testAddItemWithZeroQuantityThrowsException() throws IncorrectItemException {
		// ARRANGE
		mockItem = new MockItem(product1, 0, 10.0);
		
		// ACT & ASSERT
		assertThrows(IncorrectItemException.class, () -> {
			order.addItem(mockItem);
		});
		
		assertEquals(0, order.getItems().size(),
			"La orden debe permanecer vacía cuando se rechaza un item con cantidad cero");
	}
	
	/**
	 * PRUEBA 3: Validación de cantidad negativa
	 * 
	 * Descripción: Un item con cantidad negativa es inválido.
	 * 
	 * Entrada: Item con precio = 10.0, cantidad = -5
	 * Resultado esperado: Se lanza IncorrectItemException
	 */
	@Test
	@DisplayName("Rechazo de item con cantidad negativa")
	public void testAddItemWithNegativeQuantityThrowsException() throws IncorrectItemException {
		// ARRANGE
		mockItem = new MockItem(product1, -5, 10.0);
		
		// ACT & ASSERT
		assertThrows(IncorrectItemException.class, () -> {
			order.addItem(mockItem);
		});
		
		assertEquals(0, order.getItems().size(),
			"La orden debe permanecer vacía cuando se rechaza un item con cantidad negativa");
	}
	
	/**
	 * PRUEBA 4: Agregar primer item válido a orden vacía
	 * 
	 * Descripción: Cuando agregamos el primer item válido a una orden vacía,
	 * este debe ser agregado correctamente y la orden debe contener exactamente 1 item.
	 * 
	 * Entrada: Item válido (producto1, cantidad=2, precio=15.0)
	 * Resultado esperado: Orden contiene 1 item con los datos originales
	 */
	@Test
	@DisplayName("Agregar primer item válido a orden vacía")
	public void testAddFirstValidItemToEmptyOrder() throws IncorrectItemException {
		// ARRANGE
		mockItem = new MockItem(product1, 2, 15.0);
		
		// ACT
		order.addItem(mockItem);
		
		// ASSERT
		assertEquals(1, order.getItems().size(),
			"La orden debe contener exactamente 1 item después de agregarlo");
		
		// Verificamos que el item en la orden es el mismo que agregamos
		Item storedItem = order.getItems().iterator().next();
		assertEquals(2, storedItem.getQuantity(),
			"La cantidad del item debe ser 2");
		assertEquals(15.0, storedItem.getPrice(),
			"El precio del item debe ser 15.0");
		assertEquals(product1, storedItem.getProduct(),
			"El producto debe ser product1");
	}
	
	/**
	 * PRUEBA 5: Agregar múltiples items con productos diferentes
	 * 
	 * Descripción: Cuando agregamos items con productos diferentes,
	 * todos deben ser almacenados en la orden sin fusión.
	 * 
	 * Entrada: 
	 *   - Item 1: producto1, cantidad=2, precio=10.0
	 *   - Item 2: producto2, cantidad=3, precio=20.0
	 * 
	 * Resultado esperado: Orden contiene 2 items diferentes
	 */
	@Test
	@DisplayName("Agregar múltiples items con productos diferentes")
	public void testAddMultipleItemsWithDifferentProducts() throws IncorrectItemException {
		// ARRANGE
		MockItem item1 = new MockItem(product1, 2, 10.0);
		MockItem item2 = new MockItem(product2, 3, 20.0);
		
		// ACT
		order.addItem(item1);
		order.addItem(item2);
		
		// ASSERT
		assertEquals(2, order.getItems().size(),
			"La orden debe contener exactamente 2 items diferentes");
	}
	
	/**
	 * PRUEBA 6: Fusión de items - Mismo producto y mismo precio
	 * 
	 * Descripción: Cuando se agrega un item cuyo producto y precio coinciden
	 * con uno ya existente, las cantidades deben sumarse automáticamente.
	 * No se crea un nuevo item, sino que se actualiza el existente.
	 * 
	 * Entrada:
	 *   - Item 1: producto1, cantidad=5, precio=10.0
	 *   - Item 2: producto1, cantidad=3, precio=10.0 (mismo producto y precio)
	 * 
	 * Resultado esperado:
	 *   - La orden contiene 1 item (no 2)
	 *   - La cantidad del item es 8 (5 + 3)
	 */
	@Test
	@DisplayName("Fusión de items con mismo producto y mismo precio")
	public void testMergeItemsWithSameProductAndPrice() throws IncorrectItemException {
		// ARRANGE
		MockItem item1 = new MockItem(product1, 5, 10.0);
		MockItem item2 = new MockItem(product1, 3, 10.0);
		
		// ACT
		order.addItem(item1);
		order.addItem(item2);
		
		// ASSERT
		assertEquals(1, order.getItems().size(),
			"La orden debe contener 1 item (fusionados)");
		
		Item mergedItem = order.getItems().iterator().next();
		assertEquals(8, mergedItem.getQuantity(),
			"La cantidad debe ser la suma: 5 + 3 = 8");
		assertEquals(product1, mergedItem.getProduct(),
			"El producto debe seguir siendo product1");
		assertEquals(10.0, mergedItem.getPrice(),
			"El precio debe mantener el valor original: 10.0");
	}
	
	/**
	 * PRUEBA 7: No fusión de items - Mismo producto pero diferente precio
	 * 
	 * Descripción: Cuando se agrega un item con el mismo producto pero DIFERENTE precio,
	 * NO debe haber fusión. Ambos items deben existir en la orden de forma separada.
	 * 
	 * Entrada:
	 *   - Item 1: producto1, cantidad=5, precio=10.0
	 *   - Item 2: producto1, cantidad=3, precio=12.0 (mismo producto, DIFERENTE precio)
	 * 
	 * Resultado esperado:
	 *   - La orden contiene 2 items (NO se fusionan)
	 *   - Primer item: cantidad=5, precio=10.0
	 *   - Segundo item: cantidad=3, precio=12.0
	 */
	@Test
	@DisplayName("No fusión de items con mismo producto pero diferente precio")
	public void testNoMergeItemsWithSameProductButDifferentPrice() throws IncorrectItemException {
		// ARRANGE
		MockItem item1 = new MockItem(product1, 5, 10.0);
		MockItem item2 = new MockItem(product1, 3, 12.0);
		
		// ACT
		order.addItem(item1);
		order.addItem(item2);
		
		// ASSERT
		assertEquals(2, order.getItems().size(),
			"La orden debe contener 2 items (no se fusionan porque el precio es diferente)");
		
		// Verificamos que ambos items tienen datos correctos
		int quantitySum = order.getItems().stream().mapToInt(Item::getQuantity).sum();
		assertEquals(8, quantitySum,
			"La suma total de cantidades debe ser 8 (5 + 3)");
	}
	
	/**
	 * PRUEBA 8: Fusión múltiple - Agregar el mismo item varias veces
	 * 
	 * Descripción: Prueba que se pueden agregar el mismo item (producto y precio)
	 * múltiples veces y todas las cantidades se acumulen correctamente.
	 * 
	 * Entrada:
	 *   - Item A: producto1, cantidad=2, precio=15.0 (agregado 3 veces)
	 * 
	 * Resultado esperado:
	 *   - La orden contiene 1 item
	 *   - La cantidad final es 6 (2 + 2 + 2)
	 */
	@Test
	@DisplayName("Fusión múltiple - Agregar el mismo item varias veces")
	public void testMultipleMergesOfSameItem() throws IncorrectItemException {
		// ARRANGE
		MockItem item1 = new MockItem(product1, 2, 15.0);
		MockItem item2 = new MockItem(product1, 2, 15.0);
		MockItem item3 = new MockItem(product1, 2, 15.0);
		
		// ACT
		order.addItem(item1);
		order.addItem(item2);
		order.addItem(item3);
		
		// ASSERT
		assertEquals(1, order.getItems().size(),
			"La orden debe contener 1 item después de agregar el mismo item 3 veces");
		
		Item finalItem = order.getItems().iterator().next();
		assertEquals(6, finalItem.getQuantity(),
			"La cantidad debe acumularse: 2 + 2 + 2 = 6");
	}
	
	/**
	 * PRUEBA 9: Caso límite - Item con precio cero
	 * 
	 * Descripción: Un item con precio cero (gratuito) es válido.
	 * La validación solo rechaza precios NEGATIVOS.
	 * 
	 * Entrada: Item con precio=0.0, cantidad=5
	 * Resultado esperado: Item es agregado exitosamente
	 */
	@Test
	@DisplayName("Caso límite - Item con precio cero (gratuito)")
	public void testAddItemWithZeroPriceIsValid() throws IncorrectItemException {
		// ARRANGE
		mockItem = new MockItem(product1, 5, 0.0);
		
		// ACT
		order.addItem(mockItem);
		
		// ASSERT
		assertEquals(1, order.getItems().size(),
			"Item con precio cero debe ser válido");
		
		Item storedItem = order.getItems().iterator().next();
		assertEquals(0.0, storedItem.getPrice(),
			"El precio debe ser 0.0");
	}
	
	/**
	 * PRUEBA 10: Caso límite - Item con cantidad grande
	 * 
	 * Descripción: Verifica que el sistema puede manejar cantidades grandes.
	 * 
	 * Entrada: Item con cantidad=1000000, precio=1.0
	 * Resultado esperado: Item es agregado correctamente
	 */
	@Test
	@DisplayName("Caso límite - Item con cantidad muy grande")
	public void testAddItemWithLargeQuantity() throws IncorrectItemException {
		// ARRANGE
		mockItem = new MockItem(product1, 1000000, 1.0);
		
		// ACT
		order.addItem(mockItem);
		
		// ASSERT
		assertEquals(1, order.getItems().size());
		
		Item storedItem = order.getItems().iterator().next();
		assertEquals(1000000, storedItem.getQuantity(),
			"El sistema debe manejar cantidades grandes");
	}
	
	/**
	 * Clase Mock para Item
	 * 
	 * Implementa la interfaz Item para facilitar las pruebas unitarias.
	 * Permite crear items con valores específicos sin depender de una implementación concreta.
	 */
	private static class MockItem implements Item {
		private Product product;
		private int quantity;
		private double price;
		
		public MockItem(Product product, int quantity, double price) {
			this.product = product;
			this.quantity = quantity;
			this.price = price;
		}
		
		@Override
		public Product getProduct() {
			return product;
		}
		
		@Override
		public int getQuantity() {
			return quantity;
		}
		
		@Override
		public void setQuantity(int i) {
			this.quantity = i;
		}
		
		@Override
		public double getPrice() {
			return price;
		}
	}

}
