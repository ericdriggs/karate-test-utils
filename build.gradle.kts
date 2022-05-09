plugins {
    id("java-library")
    id("org.asciidoctor.jvm.convert").version("3.3.2")
    id("io.github.gradle-nexus.publish-plugin").version("1.1.0")
}

val karateVersion = "1.2.0"
val jacksonDatabindVersion = "2.13.2.2"
val lombokVersion = "1.18.16"
val javaVersion = JavaVersion.VERSION_11

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.36")

    testImplementation("com.intuit.karate:karate-junit5:${karateVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonDatabindVersion}")

    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor ("org.projectlombok:lombok:${lombokVersion}")
}


repositories {
    mavenCentral()
    // mavenLocal()
}


nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("OSSRH_USER") ?: "")
            password.set(System.getenv("OSSRH_PASSWORD") ?: "")
        }
    }
}

allprojects {

    apply<JacocoPlugin>()

    configure<JacocoPluginExtension> {
        toolVersion = "0.8.7"
    }

    group = "io.github.ericdriggs"
    version = "0.0.1-SNAPSHOT"


    repositories {
        mavenLocal()
        mavenCentral()
    }



    tasks {
        withType<Test> {
            useJUnitPlatform()
            systemProperties(System.getProperties().toMap() as Map<String, Object>)
            systemProperties["user.dir"] = workingDir
        }

        withType<JacocoReport> {
            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }
            classDirectories.setFrom(
                files(classDirectories.files.map {
                    fileTree(it) {
                        exclude("io/github/ericdriggs/gen/**")
                    }
                })
            )
        }
    }

    apply<JavaLibraryPlugin>()

    apply<SigningPlugin>()
    apply<MavenPublishPlugin>()

    configure<JavaPluginExtension> {
        withJavadocJar()
        withSourcesJar()
    }


    configure<SigningExtension> {
        val key = System.getenv("SIGNING_KEY") ?: ""
        val password = System.getenv("SIGNING_PASSWORD") ?: ""
        val publishing: PublishingExtension by project

        useInMemoryPgpKeys(key, password)
        sign(publishing.publications)
    }

    configure<PublishingExtension> {
        publications {
            val main by creating(MavenPublication::class) {
                from(components["java"])

                versionMapping {
                    allVariants {
                        fromResolutionResult()
                    }
                }

                pom {
                    name.set("karate-test-util :: ${project.name}")
                    description.set("test utils for karate dsl:: ${project.name}")
                    url.set("https://github.com/ericdriggs/karate-test-util")
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("ericdriggs")
                            name.set("Eric Driggs")
                            email.set("open-a-github-issue@not-really-an-email-address.com")
                        }
                    }
                    scm {
                        connection.set("scm:git@github.com:ericdriggs/karate-test-util.git")
                        url.set("https://github.com/ericdriggs//karate-test-util")
                    }
                }
            }
        }
    }
}


tasks {
    register<Javadoc>("javadocs") {
        group = "Documentation"
        //destinationDir = reporting.file("$buildDir/docs/javadoc")
        title = project.name
        //destinationDir = file("$buildDir/docs/javadoc")
        with(options as StandardJavadocDocletOptions) {
            links = listOf(
                "https://docs.oracle.com/javase/8/docs/api/",
                "https://junit.org/junit5/docs/current/api/",
                "https://sdk.amazonaws.com/java/api/latest/",
                "https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/"
            )
        }
        subprojects.forEach { subproject ->
            subproject.tasks.withType<Javadoc>().forEach { task ->
                source += task.source
                classpath += task.classpath
                includes += task.includes
                excludes += task.excludes
            }
        }
    }


}
