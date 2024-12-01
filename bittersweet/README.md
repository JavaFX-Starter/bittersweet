## Java项目（Maven构建）中引入此依赖

### Maven允许加载SNAPSHOT版本的依赖

```xml

<repositories>
    <repository>
        <id>nexus</id>
        <url>http://192.168.50.139:8081/repository/maven-public</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

```xml

<pluginRepositories>
    <pluginRepository>
        <id>nexus</id>
        <url>http://192.168.50.139:8081/repository/maven-public</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </pluginRepository>
</pluginRepositories>
```

### 依赖

```xml

<dependency>
    <groupId>com.icuxika.bittersweet</groupId>
    <artifactId>bittersweet</artifactId>
    <version>0.0.2-SNAPSHOT</version>
</dependency>
```

### Kotlin插件

```xml

<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>2.1.0</version>
    <executions>
        <execution>
            <id>compile</id>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <sourceDirs>
                    <sourceDir>${project.basedir}/src/main/kotlin</sourceDir>
                    <sourceDir>${project.basedir}/src/main/java</sourceDir>
                </sourceDirs>
            </configuration>
        </execution>
    </executions>
</plugin>
```