require 'buildr/git_auto_version'
require 'buildr/custom_pom'

desc 'Jeo: Java Geo library'
define 'jeo' do
  project.group = 'org.realityforge.jeo'

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/jeo')
  pom.add_developer('realityforge', 'Peter Donald')
  pom.provided_dependencies.concat [:javax_annotation, :javax_json]

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  compile.with :javax_json,
               :slf4j_api,
               :slf4j_jdk14,
               :jts,
               :geolatte_geom,
               :javax_annotation

  test.using :testng

  package(:jar)
  package(:sources)
  package(:javadoc)
end
