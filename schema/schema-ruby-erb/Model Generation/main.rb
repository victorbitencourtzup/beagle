#   Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA

#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
 
#       http://www.apache.org/licenses/LICENSE-2.0
 
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

require 'erb'
require 'date'

require './Synthax/variable.rb'
require './Synthax/type.rb'
require './Models/Widgets/button.rb'
require './Models/Widgets/text.rb'
require './FileHandler/file_handler.rb'
require './Common/constants.rb'

class ModelGenerator
  
  def initialize(components)
    @objectType = nil
    @erb = nil
    @writer = FileHandler.new
    @components = components
  end
  
  attr_accessor :objectType

  def to_s
    @erb.result(binding)
  end

  def generateKotlin
    @erb = ERB.new(File.read("model_template_kotlin.erb"), nil, '-')
    for component in @components
      @objectType = component.new
      @writer.write(Constants.new.kotlin_path + @objectType.fileName + "kt", to_s)
    end
  end
  
  def generateSwift()
    @erb = ERB.new(File.read("model_template_swift.erb"), nil, '-')
    for component in @components
      @objectType = component.new
      @writer.write(Constants.new.swift_path + @objectType.fileName + "swift", to_s)
    end
  end

  def generateTs
    @erb = ERB.new(File.read("model_template_ts.erb"), nil, '-')
    for component in @components
      @objectType = component.new
      @writer.write(Constants.new.ts_path + @objectType.fileName + "ts", to_s)
    end
  end

end

if __FILE__ == $0
  components = [
    Button,
    Text
  ]
  
  g = ModelGenerator.new(components)
  
  g.generateSwift
  g.generateKotlin
  g.generateTs

end