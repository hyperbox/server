.PHONY: init clean
default: init;

clean:
	rm -rf build

vendor/vbox:
	mkdir -p $@
vendor/vbox/vboxjws-6.0.jar: vendor/vbox
	mkdir -p build/tmp/vbox/6_0
	wget -P build/tmp/vbox/6_0 -N https://download.virtualbox.org/virtualbox/6.0.24/VirtualBoxSDK-6.0.24-139119.zip
	unzip -o build/tmp/vbox/6_0/VirtualBoxSDK-6.0.24-139119.zip sdk/bindings/webservice/java/jax-ws/vboxjws.jar -d build/tmp/vbox/6_0/
	cp build/tmp/vbox/6_0/sdk/bindings/webservice/java/jax-ws/vboxjws.jar $@
vendor/vbox/vboxjws-6.1.jar: vendor/vbox
	mkdir -p build/tmp/vbox/6_1
	wget -P build/tmp/vbox/6_1 -N https://download.virtualbox.org/virtualbox/6.1.32/VirtualBoxSDK-6.1.32-149290.zip
	unzip -o build/tmp/vbox/6_1/VirtualBoxSDK-6.1.32-149290.zip sdk/bindings/webservice/java/jax-ws/vboxjws.jar -d build/tmp/vbox/6_1/
	cp build/tmp/vbox/6_1/sdk/bindings/webservice/java/jax-ws/vboxjws.jar $@

init: vendor/vbox/vboxjws-6.0.jar vendor/vbox/vboxjws-6.1.jar
