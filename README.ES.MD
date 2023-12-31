# Plantilla MVVM Kotlin MultiPlatform

Esta plantilla está pensada para facilitar el desarrollo de aplicaciones multiplataforma, utilizando
el framework [KMP (Kotlin MultiPlatform)](https://kotlinlang.org/docs/multiplatform.html). Además se 
ha dotado de las dependencias necesarias para que, **utilizando una única base de código**, podamos 
construir tanto las capas de negocio como las de presentación.

Para la capa de presentación se han agregado las dependencias necesarias para poder diseñar las 
interfaces de usuario utilizando [JetpackCompose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/),
lo que va a redundar en un mayor control, y sobre todo, una base de código común en la que no tendremos que
diseñar UI específicas a cada plataforma.

A continuación, se expondrán tanto las preconfiguraciones previas a realizar, como una explicación exhaustiva
de lo que la plantilla ofrece en cuanto a arquitectura, librerías y funcionalidades desplegadas.

## Configuración previa

La primera vez que creemos un proyecto multiplatform, vamos a necesitar de varias configuraciones previas, en los
entornos MacOS (el desarrollo para iOS tiene sus cosillas), las cuales nos ayudarán en la correcta configuración 
del sistema, evitando errores posteriores de compilación. Por tanto, deberemos prestar especial atención a
los siguientes puntos:

- **Comunes a todos los Sistemas Operativos (Windows, MacOs, Linux)**:
  - Instalar el JDK 17, si no lo tenemos instalado aún.
  - Agregar en la raíz del proyecto un fichero local.properties en el que establezcamos el path
  al SDK de Android.
  - Agregar el plugin [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
- **Específico en sistemas MacOs (para desarrollo iOS)**:
  - Comprobar que el sistema cumple los requisitos mínimos para desarrollos KMP para iOS con
    [KDoctor](https://github.com/Kotlin/kdoctor).

## Ejecución de la app en las distintas plataformas

Se han proporcionado tres tareas de proyecto para la ejecución de la app desarrollada en cada plataforma,
de este modo podremos ahorrarnos el proceso de lanzarlas desde consola con las correspondientes tareas
de gradle:

![app_execution_tasks.png](art/app_execution_tasks.png)

Aún así se podrán ejecutar lanzando las siguientes tareas:

### Android

- Ejecutar la tarea `./gradlew :presentation:ui:assembleDebug`
- Buscar el fichero `.apk` en la ruta `presentation/ui/build/outputs/apk/debug/composeApp-debug.apk`

### Escritorio

- Lanzar la aplicación de escritorio con la tarea `./gradlew :presentation:ui:run`

### iOS

Para lanzar la aplicación en un emulador iOS, tendremos dos posibilidades:
- Abrir el proyecto `iosApp/iosApp.xcproject` en XCode y ejecutarlo con la configuración estándar.
- Utilizando el plugin KMP, desde el propio Android Studio.

## Arquitectura del Proyecto

La Arquitectura de este proyecto se basa en el clásico Clean Architecture, con algunas particularidades
que, creo, lo hacen interesante tanto a nivel didáctico como productivo, ya que proporciona una separación
total de cada capa, permitiendo un desacople máximo en cada sección. Así pues, de forma esquemática (sin entrar
en la estructura interna creada para KMP), podemos ver todos los módulos definidos en la siguiente imagen:

![MVVM KMM Template.png](art/MVVM_KMM_Template.png)

Como podemos apreciar en la imagen, el proyecto se divide de forma "superficial" en cuatro capas, las cuales a 
su vez implementarán cada módulo que abstraerá y protegerá el acceso indebido desde un módulo a otro.
A continuación se expone la funcionalidad de cada capa, así como la de cada módulo que la compone:

- **core**, es una capa transversal, la cual dispone de módulos accesibles a capas concretas, para acciones concretas
pero a su vez, dispone de módulos visibles para todas las capas, en los que se proporcionan funcionalidades que 
pueden ser de utilidad común. Veamos cada uno de los módulos que componen esta capa:
  - *common*, este módulo es visible para todas las capas y proporciona métodos útiles que pueden ser usados en cualquiera
  de las capas del proyecto (por ejemplo, proporciona métodos que nos indican el sistema en el que se ejecuta la app).
  - *viewmodels*, este módulo sólo es visible por el módulo de viewmodels de la capa **presentation**, y proporciona
  funcionalidad básica para gestión y manipulación de estados en los viewmodels.
  - *di*, este módulo tan solo recopila los distintos objetos de inyección de dependencia de cada módulo, para proporcionarlos
  al **módulo ui de la capa presentation**, a fin de gestionar correctamente el árbol de dependencias.
  - *ui*, este módulo sólo es visible por el **módulo ui de la capa presentation**, se proporciona en este módulo tanto el tema
  de JetpackCompose, como composables de uso general. Lo ideal es que esta capa albergue abstracciones de los componentes a utilizar,
  ya que de este modo, si se modifica algún elemento de compose, la UI principal no se verá afectada. Por ejemplo, podemos 
  desarrollar los composables de TextField, Button, PasswordTextField, etc, adaptados al theming que requiramos.
- **data**,
  - *models*,
  - *repository*,
  - *datasources*,
  - *datasources-core*,
- **domain**,
  - *models*,
  - *repository*,
  - *usecases*
- **presentation**,
  - *viewmodels*,
  - *presentation*,
  


