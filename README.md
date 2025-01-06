<div align="center">
  <h1 align="center"><a href="https://github.com/ASJordi/exchange-rate-mx-usd">Exchange Rate mx-usd</a></h1>
</div>

## About :computer:

Aplicación de consola que permite consultar el tipo de cambio por día del Peso Mexicano (MXN) en relación con el Dólar Estadounidense (USD) usando la API de [banxico.org.mx](https://www.banxico.org.mx/SieAPIRest/service/v1/), y almacenar la información en un archivo con formato JSON.

La aplicación guarda los datos históricos en un archivo JSON de acuerdo a los registros de la propia API y se actualiza de manera automática cada día mediante el uso de GitHub Actions y un cron job.

## Características :sparkles:

- Realizar y procesar peticiones a una API.
- Convertir la respuesta de la API a un objeto Java.
- Actualizar un archivo JSON con la información obtenida.
- Convertir un objeto Java a un archivo JSON.
- Automatizar la actualización del archivo JSON mediante GitHub Actions.

## Clases :books:

- `Paquete model`: Contiene las clases que representan los datos obtenidos de la API.
- `FileUtils`: Clase con métodos estáticos para leer y escribir en archivos de texto.
- `RequestManager`: Clase encargada de realizar la petición a la API y procesar la respuesta.
- `DataMapper`: Clase encargada de mapear los datos obtenidos de la API a un objeto Java, y viceversa.
- `BmxDataProcessor`: Clase encargada de procesar los datos obtenidos de la API y actualizar el archivo JSON.
- `Main`: Clase principal que ejecuta la aplicación.

## Tecnologías :gear:

- Java 21
- Maven
- Lombok
- Jackson Databind

## Instalación :floppy_disk:

1. Clonar el repositorio.
2. Abrir el proyecto en un IDE.
3. Instalar las dependencias necesarias.
4. Definir la variable de entorno `API_TOKEN_BMX` con el valor de tu token de [banxico.org.mx](https://www.banxico.org.mx/SieAPIRest/service/v1/token).
5. Ejecutar el programa.

## License :page_facing_up:

Distribuido bajo la licencia MIT. Consulte `LICENSE` para obtener más información.

## Contacto :email:

Jordi Ayala - [@ASJordi](https://x.com/ASJordi)

Link del proyecto: [https://github.com/ASJordi/exchange-rate-mx-usd](https://github.com/ASJordi/exchange-rate-mx-usd)