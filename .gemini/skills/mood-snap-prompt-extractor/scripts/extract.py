import os
import xml.etree.ElementTree as ET

def extract_prompts():
    xml_path = '.idea/project.prompts.xml'
    prompts_dir = 'prompts'
    
    if not os.path.exists(xml_path):
        print(f"Error: {xml_path} not found.")
        return

    if not os.path.exists(prompts_dir):
        os.makedirs(prompts_dir)

    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        
        # Find the list of prompts
        # Structure: component[@name="PromptTemplates"] -> option[@name="prompts"] -> list -> PromptTemplate
        prompts_list = root.find(".//option[@name='prompts']/list")
        if prompts_list is None:
            print("No prompts found in the XML.")
            return

        for template in prompts_list.findall('PromptTemplate'):
            name = ""
            text = ""
            for option in template.findall('option'):
                opt_name = option.get('name')
                opt_value = option.get('value')
                if opt_name == 'name':
                    name = opt_value
                elif opt_name == 'text':
                    text = opt_value
            
            if name and text:
                # Replace spaces with underscores
                clean_name = name.replace(' ', '_')
                if not clean_name.endswith('.md'):
                    clean_name += '.md'
                file_path = os.path.join(prompts_dir, clean_name)
                
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(text)
                print(f"Updated/Created: {file_path}")

    except ET.ParseError as e:
        print(f"Error parsing XML: {e}")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    extract_prompts()
