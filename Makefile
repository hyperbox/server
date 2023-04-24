.PHONY: init clean
default: init;

clean:
	rm -rf build
	rm -rf vendor/vbox

vendor/vbox:
	mkdir -p $@
vendor/vbox/vboxjws-6.0.jar: vendor/vbox
	mkdir -p build/tmp/vbox/6_0
	wget -N https://download.virtualbox.org/virtualbox/6.0.24/VirtualBoxSDK-6.0.24-139119.zip -O build/tmp/vbox/6_0/sdk.zip
	unzip -o build/tmp/vbox/6_0/sdk.zip sdk/bindings/webservice/java/jax-ws/vboxjws.jar -d build/tmp/vbox/6_0/
	cp build/tmp/vbox/6_0/sdk/bindings/webservice/java/jax-ws/vboxjws.jar $@
vendor/vbox/vboxjws-6.1.jar: vendor/vbox
	mkdir -p build/tmp/vbox/6_1
	wget -N https://download.virtualbox.org/virtualbox/6.1.44/VirtualBoxSDK-6.1.44-156814.zip -O build/tmp/vbox/6_1/sdk.zip
	unzip -o build/tmp/vbox/6_1/sdk.zip sdk/bindings/webservice/java/jax-ws/vboxjws.jar -d build/tmp/vbox/6_1/
	cp build/tmp/vbox/6_1/sdk/bindings/webservice/java/jax-ws/vboxjws.jar $@
vendor/vbox/vboxjws-7.0.jar: vendor/vbox
	mkdir -p build/tmp/vbox/7_0
	wget -N https://download.virtualbox.org/virtualbox/7.0.8/VirtualBoxSDK-7.0.8-156879.zip -O build/tmp/vbox/7_0/sdk.zip
	unzip -o build/tmp/vbox/7_0/sdk.zip sdk/bindings/webservice/java/jax-ws/vboxjws.jar -d build/tmp/vbox/7_0/
	cp build/tmp/vbox/7_0/sdk/bindings/webservice/java/jax-ws/vboxjws.jar $@

init: vendor/vbox/vboxjws-6.0.jar vendor/vbox/vboxjws-6.1.jar vendor/vbox/vboxjws-7.0.jar
	rm -rf build/tmp/vbox
