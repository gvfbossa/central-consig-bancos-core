import os
import subprocess

ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', '..', '..'))

def run_command(description, command):
    print(f"\n{description}")
    result = subprocess.run(command, shell=True, cwd=ROOT_DIR)
    if result.returncode != 0:
        print(f"Erro ao executar: {command}")
        exit(1)

# 1. mvn clean
run_command("Limpando build com Maven", "mvn clean")

# 2. git add .
run_command("Adicionando arquivos ao Git", "git add .")

# 3. git commit -m "mensagem"
commit_msg = input("\nDigite a mensagem do commit: ")
run_command("Commitando alterações", f'git commit -m "{commit_msg}"')

# 4. git push origin master
run_command("Enviando para o repositório remoto", "git push origin master")

# 5. mvn install
run_command("Instalando build localmente", "mvn install")

print("\nCore atualizado e buildado com sucesso.")
