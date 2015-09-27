import os
import sys
import virtualenv
from imp import reload
from codecs import open
from shutil import copyfile
from os.path import join
from setuptools import setup, find_packages

# Folder containing setup.py
root = os.path.dirname(os.path.abspath(__file__))

python_path = os.getenv('scripting.python.path', None)
module_name = os.getenv('MODULE_NAME', '')
venv_name = os.getenv('V_ENV', '.v_env')
venv_path = join(root, venv_name)

activate_files = [join(venv_path, r'Scripts', 'activate_this.py'),
                  join(venv_path, r'bin', 'activate_this.py')]
python_files = [join(sys.prefix, r'bin', 'python'),
                join(sys.prefix, r'Scripts', 'python.exe')]
requirements_file = ['requirements.txt']

def create_venv():
    if not os.path.exists(venv_path):
        try:
            virtualenv.create_environment(venv_name)
        except OSError as err:
            if err.errno == 71 or err.errno == 30:
                virtualenv.create_environment(venv_name, symlink=False)
            else:
                raise err


def get_activate_file():
    for file in activate_files:
        if os.path.exists(file):
            return file
    raise EnvironmentError('virtualenv acitvate file not found %s' % str(activate_files))

def get_requirements_file():
    for file in requirements_file:
        if os.path.exists(file):
            return file
    raise EnvironmentError('virtualenv requirements file not found %s' % str(requirements_files))

def get_python_file():
    if python_path is not None:
        return python_path
    for file in python_files:
        if os.path.exists(file):
            return file
    raise EnvironmentError('python file not found %s' % str(python_files))


def reload_imports():
    import pkg_resources
    reload(pkg_resources)
    import setuptools
    reload(setuptools)


def activate_venv():
    with open(get_activate_file()) as f:
        code = compile(f.read(), get_activate_file(), 'exec')
        exec(code, dict(__file__=get_activate_file()))


def read_requirements():
    content = [line.rstrip('\n') for line in open(get_requirements_file())]
    return content

create_venv()
activate_venv()
reload_imports()
require=read_requirements()
print require


setup(
    name="scripting",
    packages=find_packages(),
    zip_safe=False,
    platforms='any',
    install_requires=require,
    tests_require=[
        'unittest2',
        'nose',
        'coverage',
        'mock',
        'responses'],
    author="sumanta",
    description="python scripting support",
    license="",
    classifiers=[
        'Development Status :: 1 - Beta',
        'Environment :: Shell Scripting',
        'Intended Audience :: Developers',
        'Operating System :: OS Independent',
        'Programming Language :: Python',
        'Topic :: Software Development :: Libraries :: Python Modules'
    ],
)
