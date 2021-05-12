# cristalix-npcs

## Установка

Плагин cristalix-npcs необходимо [скачать](https://repo.implario.dev/public/ru/cristalix/npcs-bukkit-api) и установить, а затем добавить в `plugin.yml` в качестве зависимости.

```yml
name: Example plugin
main: com.example.Example
version: 1.0
depends: [NPCs]
```

И добавить библиотеку в `build.gradle`, если вы используете Gradle.

```groovy
repositories {
    maven {
        url 'https://repo.implario.dev/public'
    }
}

dependencies {
    compileOnly 'ru.cristalix:npcs-bukkit-api:3.1.0'
}
```

Или в `pom.xml`, если Maven.

```xml
<repositories>
    <repository>
        <id>implario-public</id>
        <url>https://repo.implario.dev/public</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ru.cristalix</groupId>
        <artifactId>npcs-bukkit-api</artifactId>
        <version>3.1.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

<details>
  <summary>Установка до 3.1.0</summary>

### До 3.1.0

Проект cristalix-npcs необходимо включать в один из своих плагинов.

```groovy
repositories {
    maven {
        url 'https://repo.implario.dev/public'
    }
}

dependencies {
    implementation 'ru.cristalix:npcs-bukkit-api:3.0.0'
}
```

</details>


## Использование

```java

// Создание NPC
Npc npc = Npc.builder()
        .location(location)
        .name(name)
        .skinUrl(url) // Ссылка на скин
        .skinDigest(digest) // Хэш скина
        .slimArms(false)
        .behaviour(NpcBehaviour.STARE_AT_PLAYER) // Поведение NPC
        .build();

Npcs.spawn(npc); // Добавление NPC в мир
Npcs.remove(npc); // Удаление NPC из мира

Npcs.show(npc); // Отправка NPC определённому игроку
Npcs.hide(npc); // Скрытие NPC у определённого игрока


```