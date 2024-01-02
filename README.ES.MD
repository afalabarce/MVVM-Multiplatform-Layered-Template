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

Teniendo en cuenta lo siguiente (siempre pensando en Origen -> Destino):
- <span style="color:#339966">**Flechas Verdes**</span>, Módulo A es dependencia de Módulo B
- <span style="color:#ff0000">**Flechas Rojas**</span>, Módulo A utiliza funcionalidad de Módulo B
- <span style="color:#ff99cc">**Flechas Rosas**</span>, Módulo A es implementado en Módulo B

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
- **data**, esta capa es la responsable de gestionar **todos** los orígenes de datos de los que se va a alimentar la app. idealmente,
gestionará incluso las posibles obtenciones de datos de gps, bluetooth, etc, además de las clásicas peticiones a servicios REST,
Bases de datos locales, gestión de preferencias, etc. A continuación, se comenta la funcionalidad y responsabilidad de cada uno de los
módulos que componen esta capa:
  - *models*, este módulo mantiene, idealmente separados por feature, los modelos de datos tanto de bases de datos locales como remotos.
  - *repository*, en este módulo **se implementan las interfaces de repositorio definidas en el módulo domain:repository**, además es el
  responsable de la implementación / definición de los distintos mapeadores hacia/desde entidades de dominio (**domain:models**) a entidades
  de bases de datos o remotas.
  - *datasources*, en este módulo se definen las interfaces que gestionarán todas y cada una de las peticiones a orígenes de datos. Este módulo
  está **referenciado por el módulo de data:repository**.
  - *datasources-core*, este módulo es el responsable de centralizar todas las implementaciones de las interfaces definidas en **data:datasources**,
  siendo por tanto el **único módulo de todo el proyecto con capacidad de conexión a sistemas externos y/o bases de datos locales**.
- **domain**, En esta capa gestionamos la lógica de negocio que aglutina los métodos de los repositorios en las distintas clases de caso de uso, a fin
de acotar y atomizar las peticiones, para utilizar única y exclusivamente las funcionalidades necesarias en un momento dado.
  - *models*, en este módulo definiremos, idealmente, por feature los distintos modelos de datos de dominio, que serán utilizados por las capas de presentación
  y que se nos proporcionan ya mapeados desde los repositorios.
  - *repository*, en este módulo se definen las interfaces de comunicación que serán implementadas en el módulo **data:repository**. Estas interfaces son,
  en último término, el contrato de comunicación entre la capa de acceso a datos y la capa de presentación.
  - *usecases*, este módulo es el responsable, organizando todo por feature, de la abstracción de las llamadas de los repositorios acotando cada caso de uso
  a una funcionalidad concreta, esto quiere decir que un caso de uso no debería implementar más de una funcionalidad, por ejemplo, GetDeviceIdUseCase, 
  SetDeviceIdUseCase, etc.
- **presentation**, esta es la capa responsable de la interactuación con el usuario, en la que definiremos tanto la UI como el sistema de obtención de datos
desde la capa de dominio, a través de ViewModels.
  - *viewmodels*, este módulo es el responsable, organizado por feature, de gestionar las distintas peticiones y manipulación de datos y estados, a partir de los
  casos de uso, a través del módulo **domain:usecases**, proporcionando a la UI la información de forma reactiva, garantizando por tanto que, aparte de 
  que la UI no quedará congelada, tendremos un único punto de recolección y actualización de datos.
  - *ui*, este módulo se encargará de la presentación de la información proporcionada por los distintos viewmodels definidos en el módulo
  **presentation:viewmodels**, debemos prestar atención a que idealmente **no deberíamos utilizar componentes genéricos de compose, en beneficio de componentes
  propios**, ya que de este modo, conseguiremos extraer la responsabilidad de futuros cambios de funcionalidad o apariencia a un único punto.

En este punto, hemos definido qué hace cada módulo en cada capa, pero queda pendiente explicar / definir lo expuesto en el esquema gráfico. 
Veamos por módulo cuales son sus dependencias:

- **Prácticamente todos los módulos**, dependen de core -> common, por lo que no es necesario indicar esta dependencia.
- **domain -> repository**, requiere de la inclusión de las siguientes dependencias:
  - *domain -> models*, ya que en [domain -> repository] es donde se definen las interfaces que van a proporcionar los datos obtenidos de los distintos orígenes de datos
  ya mapeados a las entidades de dominio definidas en esta capa.
- **domain -> usecases**, se definen en este módulo las siguientes dependencias:
  - *domain -> models*, ya que, [domain -> usecases] tiene como valores de retorno entidades de dominio, requiere de este módulo para poder realizar esta labor.
  - *domain -> repository*, los casos de uso definidos en [domain -> usecases] requieren de los datos que se devolverán desde las funciones que se definen en las interfaces
  descritas en este módulo.
- **data -> datasources**, tiene las  siguientes dependencias:
  - *data -> models*, ya que requiere los modelos de datos (locales y remotos)
- **data -> datasources-core**, requiere las siguientes dependencias:
  - *data -> datasources*, ya que es en [data -> datasources-core] donde se realiza la implementación de las interfaces definidas
  en este módulo.
  - *data -> models*, ya que requiere los modelos de datos (locales y remotos) para la implementación de las interfaces definidas en el punto anterior.
- **data -> repository**, requiere las siguientes dependencias:
  - *data -> datasources*, ya que es [data -> repository] donde se realiza la interpretación de los resultados de datasources y su adaptación a entidades de dominio.
  - *data -> models*, ya que utiliza y es necesaria por los resultados proporcionados por la dependencia anterior.
  - *domain -> models*, ya que es en [data -> repository] donde se realiza el mapeo de entidades de datos (locales y remotas) a entidades de dominio.
  - *domain -> repository*, ya que en [data -> repository] es donde se implementan las interfaces definidas en este módulo.
- **presentation -> viewmodels**, esta capa requiere las siguientes dependencias:
  - *domain -> models*, en las capas de presentación y dominio, tan solo recolectamos datos en base a las entidades de dominio definidas en este módulo.
  - *domain -> usecases*, los viewmodels tienen como única fuente de datos a los resultados proporcionados por los distintos casos de uso definidos en este módulo.
  - *core -> viewmodels*, esta dependencia proporciona ciertas funcionalidades que pueden ser aprovechadas por el módulo de [presentation -> viewmodels].
- **presentation -> ui**, la capa de presentación presenta las siguientes dependencias:
  - *domain -> models*, en la capa de presentación, tan solo recolectamos datos en base a las entidades de dominio definidas en este módulo.
  - *presentation -> viewmodels*, la capa de presentación, obtiene y proporciona feedback a recursos de datos tanto locales como remotos **únicamente a través de los viewmodels
  relacionados**.
  - *core -> di*, ya que el proveedor de dependencias (Koin) debe gestionarse desde la capa de presentación es preciso pasarle el árbol de dependencias de forma aséptica, lo que
  conseguimos incluyendo esta dependencia y un objeto concreto que define todo el árbol.
  - *core -> ui*, gracias a este módulo, vamos a centralizar toda la apariencia y theming de la app, para ello, es en este módulo donde se definirá el tema la app, así como los
  componentes básicos (ya con los valores de tema aplicados) que se van a utilizar en la app.

Como podemos apreciar, en base a la descripción de las distintas dependencias que hemos definido, tenemos la seguridad de que cada capa sólo es visible por la capa / módulo correcta
evitando por completo posibles accesos indebidos, ya sea de forma intencionada o errónea.
  
## Funcionalidades implementadas (por capa)

### Core

En la capa core, se implementan algunas de las funcionalidades básicas de toda la app, particularmente se gestionan los distintos elementos que van a permitirnos gestionar
tanto el árbol de inyección de dependencias como la gestión de Viewmodels. Veamos, por cada módulo las posibiliades más interesantes que nos proporciona:

#### Common

En este módulo se define una de las funcionalidades más importantes de todo el proyecto, ya que nos permite gestionar de forma relativamente sencilla todo el proceso de provisión de 
dependencias en todo el proyecto (de ahí que este módulo sea dependencia directa del resto de módulos del proyecto).

En el package [io.github.afalabarce.mvvmkmmtemplate.core.common.di] se define la interfaz **KoinModuleLoader**, la cual expone una propiedad de solo lectura
llamada **koinModules**, que no es más que una lista de módulos de koin.
Esta interfaz la implementaremos en cada módulo que requiera de inyección de dependencias, por ejemplo,
[data -> datasources-core], [data -> repository], [domain -> usecases] y [presesntation -> viewmodels].

Su utilización es muy sencilla, veámoslo con un ejemplo de código (extraido de [data -> datasources-core]):

```kotlin
object DataSourceCoreDependencyInjector: KoinModuleLoader {
    override val koinModules: List<Module>
        get() = getPlatformInjects().union(
            listOf(
                module {
                    single <ApiService>{
                        Ktorfit
                            .Builder()
                            .baseUrl(ApiService.API_URL)
                            .build()
                            .create()
                    }
                    singleOf(::Database)
                    single<AppPreferences>{ AppPreferencesImpl(get()) }
                }
            )
        ).toList()
}
```

Como vemos, lo más sencillo es crear un objeto que implemente la interfaz, y directamente definir el módulo con las dependencias propias del módulo.
Además se proporciona un método getPlatformInjects que está marcado como **expected** a fin de que en cada plataforma definamos las dependencias que corresponda, es decir, en función de una plataforma 
u otra, se definirán ciertas dependencias que sean requeridas por un sistema u otro (**ver ejemplos definidos en la capa**).

Aparte, dispone de ciertas funciones de extensión, que de un modo u otro nos van a facilitar diversas operaciones (sobre todo formateo de objetos y conversiones seguras entre tipos).

#### ViewModels

En este módulo tan solo se ha definido una clase KmmViewModel (hereda de Viewmodel) que implementa un método de extensión a los CoroutineScope que permite una ejecución segura de un método de suspensión, ya que nos permite gestionar de forma 
sencilla los posibles errores. Así pues, nuestros ViewModel deberán ser similares al siguiente ejemplo:

```kotlin

class MyViewModel(
    private val myUseCase: MyUseCase
): KmmViewModel() {
    val myUseCaseResult by lazy { myUseCase.flowResult.stateIn(viewModelScope, SharingStarted.Lazily, initialValue) }
  
    fun queryResult() {
        viewModelScope.safeLaunch(
            onStart = { /* do on start */ },
            onFinish = { /* do on finish */ },
            onError = { /* do on raised error */ },
            onStartContext = Dispatchers.Main,
            onFinishContext = Dispatchers.Main
        ) {
            // Suspend query calls
        }
    }
}

```

#### Di

#### Ui

### Capa data

Es en esta capa donde disponemos de más funcionadlidades implementadas y quizá las más complejas de describir, para ello se va a intentar ser lo más explícito posible en la explicación 
de las funcionalidades, así como su uso / despliegue.

### Capa domain

### Capa presentation

