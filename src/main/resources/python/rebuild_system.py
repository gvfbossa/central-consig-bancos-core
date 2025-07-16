import os
import re
import subprocess
import shutil

# Caminho raiz do core
ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', '..', '..'))
CORE_TARGET_DIR = os.path.join(ROOT_DIR, 'target')

# Caminhos dos microsserviços (ajuste conforme necessário)
MICROSERVICOS = [
    os.path.abspath(os.path.join(ROOT_DIR, '..', 'central-consig-bancos-endpoints')),
    os.path.abspath(os.path.join(ROOT_DIR, '..', 'central-consig-bancos-propostas')),
    os.path.abspath(os.path.join(ROOT_DIR, '..', 'central-consig-bancos-dados-cliente')),
]

def run_command(description, command, cwd=None):
    print(f"\n{description}")
    result = subprocess.run(command, shell=True, cwd=cwd)
    if result.returncode != 0:
        print(f"Erro ao executar: {command}")
        exit(1)

def git_commit_push(project_path, commit_msg):
    run_command("Adicionando arquivos ao Git", "git add .", cwd=project_path)
    run_command(f"Commitando alterações: {commit_msg}", f'git commit -m "{commit_msg}"', cwd=project_path)
    run_command("Enviando para o repositório remoto", "git push origin master", cwd=project_path)

def get_jar_file(path, prefix=None):
    if not os.path.exists(path):
        return None
    for f in os.listdir(path):
        if f.endswith('.jar'):
            if prefix and prefix not in f:
                continue
            return f
    return None

def extrair_versao_jar(nome_jar):
    match = re.search(r'central-consig-bancos-core-(\d+\.\d+\.\d+)\.jar', nome_jar)
    if match:
        return match.group(1)
    return None

def atualizar_pom_xml(pom_path, versao_antiga, versao_nova):
    if not os.path.isfile(pom_path):
        print(f"pom.xml não encontrado em {pom_path}")
        return
    with open(pom_path, 'r', encoding='utf-8') as f:
        conteudo = f.read()

    # Substitui usando função para evitar erro de \1\2 conflito
    def substituir_versao(match):
        return f"{match.group(1)}{versao_nova}{match.group(2)}"

    novo_conteudo = re.sub(
        rf'(<groupId>com\.centralconsig\.core</groupId>\s*<artifactId>central-consig-bancos-core</artifactId>\s*<version>){re.escape(versao_antiga)}(</version>)',
        substituir_versao,
        conteudo,
        flags=re.MULTILINE | re.DOTALL
    )

    if conteudo != novo_conteudo:
        with open(pom_path, 'w', encoding='utf-8') as f:
            f.write(novo_conteudo)
        print(f"pom.xml atualizado: versão {versao_antiga} -> {versao_nova}")
    else:
        print(f"Nenhuma alteração feita no pom.xml (versão {versao_antiga} não encontrada)")

def atualizar_dockerfile(dockerfile_path, versao_antiga, versao_nova):
    if not os.path.isfile(dockerfile_path):
        print(f"Dockerfile não encontrado em {dockerfile_path}")
        return
    with open(dockerfile_path, 'r', encoding='utf-8') as f:
        linhas = f.readlines()

    novo_conteudo = []
    for linha in linhas:
        nova_linha = linha.replace(versao_antiga, versao_nova)
        novo_conteudo.append(nova_linha)

    if linhas != novo_conteudo:
        with open(dockerfile_path, 'w', encoding='utf-8') as f:
            f.writelines(novo_conteudo)
        print(f"Dockerfile atualizado: versão {versao_antiga} -> {versao_nova}")
    else:
        print(f"Nenhuma alteração feita no Dockerfile (versão {versao_antiga} não encontrada)")

def copiar_jar_core_para_microsservico(micro_path, jar_core, versao_antiga, versao_nova):
    libs_dir = os.path.join(micro_path, 'libs')
    if not os.path.exists(libs_dir):
        print(f"Diretório libs não encontrado em {micro_path}")
        return

    jar_antigo = get_jar_file(libs_dir)
    if jar_antigo:
        print(f"Removendo JAR antigo: {jar_antigo}")
        os.remove(os.path.join(libs_dir, jar_antigo))

    shutil.copy2(os.path.join(CORE_TARGET_DIR, jar_core), libs_dir)
    print(f"Copiado {jar_core} para {libs_dir}")

    if versao_antiga != versao_nova:
        print(f"Versão do core atualizada no microsserviço: {versao_antiga} → {versao_nova}")

def atualizar_microsservico(ms_path, novo_jar, versao_antiga, versao_nova):
    print(f"\n--- Atualizando microsserviço em {ms_path} ---")

    # 1. mvn clean no microserviço
    run_command("Limpando build do microsserviço", "mvn clean", cwd=ms_path)

    # 2. Substitui o JAR
    copiar_jar_core_para_microsservico(ms_path, novo_jar, versao_antiga, versao_nova)

    # 3. Atualiza arquivos se necessário
    if versao_antiga and versao_antiga != versao_nova:
        atualizar_pom_xml(os.path.join(ms_path, 'pom.xml'), versao_antiga, versao_nova)
        atualizar_dockerfile(os.path.join(ms_path, 'Dockerfile'), versao_antiga, versao_nova)

    # 4. Git commit
    msg = (
        f"Atualiza core: nova build do core copiada (versão: {versao_nova})"
        if versao_antiga == versao_nova else
        f"Atualiza versão do core: {versao_antiga} -> {versao_nova}"
    )
    git_commit_push(ms_path, msg)

    # 5. Reinstala o projeto com o novo core
    run_command("Reinstalando microsserviço com novo core", "mvn install", cwd=ms_path)

def build_and_push_docker(micro_path):
    image_name = f"bossaws2024/{os.path.basename(micro_path)}:latest"

    print(f"\nBuildando imagem Docker para {image_name}...")
    run_command(f"Build Docker image: {image_name}", f"docker build -t {image_name} .", cwd=micro_path)

    print(f"Fazendo push da imagem {image_name} para o Docker Hub...")
    run_command(f"Push Docker image: {image_name}", f"docker push {image_name}", cwd=micro_path)

# --- Execução principal ---

# Etapas para o core
run_command("Limpando build com Maven", "mvn clean", cwd=ROOT_DIR)

# Commit do core
commit_msg = input("\nDigite a mensagem do commit: ")
git_commit_push(ROOT_DIR, commit_msg)

run_command("Instalando build localmente", "mvn install", cwd=ROOT_DIR)

# Nome e versão do novo JAR
novo_jar = get_jar_file(CORE_TARGET_DIR, prefix='central-consig-bancos-core')
if not novo_jar:
    print("Não foi possível localizar o JAR do core gerado.")
    exit(1)

versao_nova = extrair_versao_jar(novo_jar)
if not versao_nova:
    print("Aviso: versão do novo JAR não pôde ser extraída. Continuando sem atualizar versão nos microsserviços.")
else:
    print(f"\nNova versão do core: {versao_nova}")

# Atualiza microsserviços
for ms_path in MICROSERVICOS:
    libs_dir = os.path.join(ms_path, 'libs')
    jar_antigo = get_jar_file(libs_dir, prefix='central-consig-bancos-core')
    versao_antiga = extrair_versao_jar(jar_antigo) if jar_antigo else None

    atualizar_microsservico(ms_path, novo_jar, versao_antiga, versao_nova)

    build_and_push_docker(ms_path)

print("\nSistema rebuildado e atualizado com sucesso!")
