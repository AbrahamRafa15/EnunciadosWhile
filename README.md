# **Reconocimiento de Enunciados `while`**

## **🧠 Descripción**
Este programa en **Java** analiza un archivo de texto que contiene **enunciados `while` escritos en Kotlin**, verificando si están correctamente estructurados según una gramática específica.

El sistema **no utiliza generadores de parsers externos**. En su lugar, emplea:

- 🔍 **Expresiones regulares** para tokenizar el contenido.
- 🧠 **Un autómata de pila** personalizado que realiza el análisis sintáctico.
- 📊 Estadísticas detalladas **únicamente si el bloque es válido**.

---

## **📥 ¿Qué analiza el programa?**
El análisis incluye:

- 📌 **Número total de variables diferentes** usadas en los `while` (letras minúsculas, una sola letra).
- 📌 **Cantidad de constantes de un solo dígito** (`0–9`).
- 📌 **Número total de operadores relacionales encontrados**, incluyendo: `<`, `>`, `==`, `!=`, `<=`, `>=`.
- 📌 **Cantidad total de sentencias `while`** válidas.

> ⚠ Si alguna parte del archivo contiene errores de sintaxis, **el análisis se detiene** y se indica que la entrada es incorrecta.

---

## **🚀 ¿Cómo se utiliza?**

### **1️⃣ Requisitos previos**
- ✅ **Java 23.0.1** (recomendado)  
  🔗 [Descargar JDK 23.0.1](https://www.oracle.com/java/technologies/javase/jdk23-archive-downloads.html)

> También se puede usar con otras versiones recientes sin problemas.

---

### **2️⃣ Ejecución del programa**

#### **📂 Opción 1: Usando el explorador de archivos**
1. Descarga y descomprime el archivo fuente `BooleanExpressionParser.java` en una carpeta de tu computadora.
2. Abre una terminal y navega hasta la carpeta donde está ubicado el archivo.
3. Compila el programa con:
   ```sh
   javac BooleanExpressionParser.java
   ```
4. Ejecuta el programa con:
   ```sh
   java BooleanExpressionParser
   ```
5. Se abrirá una ventana donde podrás **seleccionar un archivo `.kt`**.
6. El programa analizará el archivo y mostrará **las estadísticas en la terminal**.

---

#### **💻 Opción 2 (opcional): Archivo desde la terminal**
Si prefieres indicar el archivo directamente sin usar el explorador de archivos, puedes hacerlo con:

```sh
java BooleanExpressionParser ruta/al/archivo.kt
```

Ejemplo:
```sh
java BooleanExpressionParser src/test1.kt
```

---

## **📌 Consideraciones**

✔️ La sintaxis válida para los bloques `while` es:

```kotlin
while (<expresión booleana>) {
    // posibles bloques anidados
}
```

✅ Las expresiones booleanas deben contener:

- Variables: una sola letra entre `a` y `z`
- Constantes: un solo dígito (`0–9`)
- Operadores: `<`, `>`, `==`, `!=`, `<=`, `>=`

❌ El programa **rechaza** entradas con:

- Paréntesis o llaves faltantes
- Operadores incorrectos (ej. `=` en vez de `==`)
- Jerarquía incorrecta de bloques

---

## **📜 Ejemplos de Entrada y Salida**

### ✅ Entrada válida:

```kotlin
while (x < y) {
    while (4 == 2) {
        while (z > a) { }
    }
}
```

📤 **Salida esperada:**
```
¡El archivo cumple con la(s) sentencia(s) esperada(s)!
===== ESTADÍSTICAS =====
Variables diferentes (una sola letra): 3
Constantes de un dígito: 1
Operadores de comparación: 3
Sentencias 'while' encontradas: 3
========================
```

---

### ❌ Entrada inválida:

```kotlin
while (x < 1)
    while (4 = 2) {
        while (z > a)
            while (1 > 1) { }
}
```

📤 **Salida esperada:**
```
La expresión a parsear es incorrecta.
```

---

## **📦 Estructura Interna**
- 🔧 **LexicalAnalyzer:** convierte el texto de entrada en una lista de tokens usando expresiones regulares.
- 📐 **Parser:** analiza la secuencia de tokens para verificar que los bloques `while` sean válidos.
- 📊 **Estadísticas:** se muestran únicamente si la validación es exitosa.
