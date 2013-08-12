require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'
require 'buildr/single_intermediate_layout'

desc 'Jeo: Java Geo library'
define 'jeo' do
  project.group = 'org.realityforge.jeo'

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

  package :jar
end
